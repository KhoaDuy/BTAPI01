package com.example.project.services.impl;

import com.example.project.dtos.request.LoginRequest;
import com.example.project.dtos.request.RegisterRequest;
import com.example.project.dtos.request.ResetPasswordRequest;
import com.example.project.dtos.response.UserResponse;
import com.example.project.dtos.response.VerifyOtpResponse;
import com.example.project.exceptions.BadCredentialsException;
import com.example.project.exceptions.InvalidParamException;
import com.example.project.exceptions.ResourceNotFoundException;
import com.example.project.models.ForgotPassword;
import com.example.project.models.User;
import com.example.project.repositories.ForgotPasswordRepository;
import com.example.project.repositories.UserRepository;
import com.example.project.services.EmailService;
import com.example.project.services.UserService;
import com.example.project.utils.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final EmailService emailService;


    @Override
    public UserResponse login(LoginRequest loginRequest) {

        Optional<User> userOptional = userRepository.findByUsernameAndPassword(loginRequest.getUsername(),
                loginRequest.getPassword());
        if(userOptional.isEmpty()){
            throw new BadCredentialsException("Tài khoản hoặc mật khẩu không chính xác");
        }
        else{
            User user = userOptional.get();
            if(!user.isActive())
                throw new BadCredentialsException("Tài khoản chưa được xác thực");
            return UserResponse.fromUser(user);
        }
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {

        if(userRepository.existsByUsername(registerRequest.getUsername()))
            throw new InvalidParamException("Username đã tồn tại");

        if(userRepository.existsByEmail(registerRequest.getEmail()))
            throw new InvalidParamException("Email đã tồn tại");

        if(!registerRequest.getPassword().equals(registerRequest.getRetypedPassword()))
            throw new InvalidParamException("Mật khẩu không giống nhau");

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .active(false)
                .otp(null)
                .build();
        userRepository.save(user);
        return UserResponse.fromUser(user);
    }

    @Override
    public UserResponse verifyUser(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user theo email"));
        if(user.isActive())
            throw new InvalidParamException("User đã xác thực");
        if(!user.getOtp().equals(otp))
            throw new BadCredentialsException("OTP sai");
        user.setActive(true);
        userRepository.save(user);
        return UserResponse.fromUser(user);
    }

    @Override
    public void sendMailActive(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user theo email"));
        if(user.isActive())
            throw new InvalidParamException("User đã xác thực");
        String otp = OtpUtil.generateOtp(6);
        user.setOtp(otp);
        userRepository.save(user);

        emailService.sendEmail(email,
                "Xác thực tài khoản",String.format("Mã OTP của bạn là: %s", otp));
    }

    @Override
    public void sendMailForgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user theo email"));
        Optional<ForgotPassword> existsForgotPassword = forgotPasswordRepository.findByUser(user);
        String otp = OtpUtil.generateOtp(6);

        if(existsForgotPassword.isEmpty()){
            ForgotPassword forgotPassword = ForgotPassword.builder()
                    .otp(otp)
                    .otpExpirationDate(LocalDateTime.now().plusMinutes(1))
                    .otpToken(null)
                    .user(user)
                    .build();
            forgotPasswordRepository.save(forgotPassword);
        }
        else{
            ForgotPassword forgotPassword = existsForgotPassword.get();
            forgotPassword.setOtp(otp);
            forgotPassword.setOtpExpirationDate(LocalDateTime.now().plusMinutes(5));
            forgotPasswordRepository.save(forgotPassword);
        }
        // Send mail
        emailService.sendEmail(email,
                "Quên mật khẩu",
                String.format("Mã OTP của bạn là: %s", otp));
    }

    @Override
    public VerifyOtpResponse verifyOtp(String otp, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user theo email"));
        Optional<ForgotPassword> existsForgotPassword = forgotPasswordRepository.findByUser(user);
        if(existsForgotPassword.isEmpty()){
            throw new InvalidParamException("User không có OTP");
        }
        ForgotPassword forgotPassword = existsForgotPassword.get();
        if(!forgotPassword.getOtp().equals(otp)){
            throw new InvalidParamException("OTP sai");
        }
        if(forgotPassword.getOtpExpirationDate().isBefore(LocalDateTime.now())){
            throw new InvalidParamException("OTP đã hết hạn");
        }
        String otpToken = UUID.randomUUID().toString();
        forgotPassword.setOtpToken(otpToken);
        forgotPasswordRepository.save(forgotPassword);
        return VerifyOtpResponse.builder()
                .userId(user.getId())
                .otpToken(otpToken)
                .build();
    }

    @Override
    public void resetPassword(long id, ResetPasswordRequest ResetPasswordRequest) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));

        Optional<ForgotPassword> existsForgotPassword = forgotPasswordRepository.findByUser(user);
        if(existsForgotPassword.isEmpty()){
            throw new InvalidParamException("User không có OTP");
        }
        ForgotPassword forgotPassword = existsForgotPassword.get();
        if(!forgotPassword.getOtpToken().equals(ResetPasswordRequest.getOtpToken())){
            throw new InvalidParamException("Invalid OTP token");
        }
        user.setPassword(ResetPasswordRequest.getNewPassword());
        userRepository.save(user);
    }


}
