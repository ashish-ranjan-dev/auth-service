package com.outing.auth.service.impl;


//import com.outing.auth.api.exception.AuthException;
import com.outing.commons.api.exception.OutingException;
import com.outing.auth.api.dto.SigninDto;
import com.outing.auth.api.dto.ResetPasswordDto;
import com.outing.auth.api.dto.SignupDto;
import com.outing.auth.api.enums.Constants;
import com.outing.auth.model.AuthUserModel;
import com.outing.auth.service.AuthUserService;
import jakarta.transaction.Transactional;
import com.outing.auth.security.model.UserModel;
import com.outing.auth.repository.UserRepository;
import com.outing.auth.security.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AuthUserServiceImpl implements AuthUserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Value("${outing.app.reset-uri}")
    private String resetUri;

    @Value("${server.port}")
    private String portNumber;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void signup(SignupDto signupDto) {
        List<String> errors = new ArrayList<>();
        String username = signupDto.getUsername();
        String password = signupDto.getPassword();
        String email = signupDto.getEmail();
        if (username == null) {
            errors.add(Constants.EMPTY_USERNAME);
        }
        if (password == null) {
            errors.add(Constants.EMPTY_PASSWORD);
        }
        if (email == null) {
            errors.add(Constants.EMPTY_EMAIL);
        }
        if(!Pattern.compile("(?=.*[a-z])").matcher(password).find()){
            errors.add("Password should contain atleast one lowercase letter");
        }
        if(!Pattern.compile("(?=.*[A-Z])").matcher(password).find()){
            errors.add("Password should contain atleast one uppercase letter");
        }
        if(!Pattern.compile("(?=.*[0-9])").matcher(password).find()){
            errors.add("Password should contain atleast one numeric value");
        }
        if(!Pattern.compile("(?=.*[@#$%^&+=])").matcher(password).find()){
            errors.add(Constants.NOSPECIALCHAR_PASSWORD);
        }

        if(!(Pattern.compile("(?=\\S+$).{8,100}").matcher(password).matches())){
            errors.add("Password should not contain whitespaces");
        }
        if(password.length()<8){
            errors.add(Constants.NOTLONG_PASSWORD);
        }
        if(userRepository.existsByUsername(signupDto.getUsername())){
            errors.add(Constants.NOTUNIQUE_USERNAME);
        }
        if(userRepository.existsByEmail(signupDto.getEmail())){
            errors.add(Constants.NOTUNIQUE_EMAIL);
        }
        if(!Objects.equals(signupDto.getPassword(), signupDto.getConfirmPassword())){
            errors.add(Constants.UNCONFIRMED_PASSWORD);
        }
        if (!errors.isEmpty()) {
            throw new OutingException(errors, HttpStatus.BAD_REQUEST);
        }
        signupDto.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        LocalDateTime registerTime = LocalDateTime.now();
        AuthUserModel authUserModel = new AuthUserModel(UUID.randomUUID().toString(), signupDto.getUsername(), signupDto.getPassword(), signupDto.getEmail(), signupDto.getName(), UUID.randomUUID().toString(), registerTime, null);
        userRepository.save(authUserModel);
        System.out.println("http://localhost:"+portNumber+"/auth/signup/"+ authUserModel.getId()+"/activate/"+ authUserModel.getActivationId());
        String subject = "Activate Your Account";
        String message = "http://localhost:"+portNumber+"/auth/signup/"+ authUserModel.getId()+"/activate/"+ authUserModel.getActivationId();
        sendEmail(email,subject,message);
    }

    @Override
    public String signin(MultiValueMap<String, Object> encodedSigninData) {
        SigninDto signinDto = new SigninDto();
        if (encodedSigninData.containsKey("username") && encodedSigninData.containsKey("password") && encodedSigninData.get("username").size() == 1 && encodedSigninData.get("password").size() == 1) {
            signinDto.setUsername(encodedSigninData.get("username").get(0).toString());
            signinDto.setPassword(encodedSigninData.get("password").get(0).toString());
        } else {
            throw new OutingException(Constants.UNPROCESSABLE_REQUEST, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        AuthUserModel user = userRepository.findByUsername(signinDto.getUsername());
        Boolean isCredentialsTrue = false;
        List<String> errors = new ArrayList<>();
        if (signinDto.getUsername() == null) {
            errors.add(Constants.EMPTY_USERNAME);
        }
        if (signinDto.getPassword() == null) {
            errors.add(Constants.EMPTY_PASSWORD);
        }
        if(user==null){
            errors.add("Username does not exist");
        }

        if (!errors.isEmpty()) {
            throw new OutingException(errors, HttpStatus.BAD_REQUEST);
        }
        try{
            UserModel authUser = (UserModel) authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinDto.getUsername(), signinDto.getPassword())).getPrincipal();

            return jwtUtils.generateToken(authUser);
        } catch (AuthenticationException ex) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if(encoder.matches(signinDto.getPassword(), user.getPassword()) && user.getUsername().equals(signinDto.getUsername())){
                if(user.getActivationId()!=null){
                    List<String> errors1 = List.of("User Is Not Activated");
                    throw new OutingException(errors1, HttpStatus.FORBIDDEN);
                }
            }

            throw new OutingException(Constants.INVALID_CREDENTIALS, HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public String activateUser(String userId, String activationId) {
        AuthUserModel registeredUser = userRepository.findById(userId).orElseThrow(() -> new OutingException(Constants.USER_NOTFOUND, HttpStatus.NOT_FOUND));
        if (registeredUser.getActivationId() == null || registeredUser.getActivationId().isEmpty()) {
            throw new OutingException(Constants.OLD_ACTIVATION, HttpStatus.BAD_REQUEST);
        }
        if (!Objects.equals(activationId, registeredUser.getActivationId())) {
            throw new OutingException(Constants.ACCESS_DENIED, HttpStatus.UNAUTHORIZED);
        }
        registeredUser.setActivationId(null);
        userRepository.save(registeredUser);
        return registeredUser.getName();
    }

    @Override
    public void initiateResetPassword(String usernameOrEmail) {
        AuthUserModel authUserModel = userRepository.findByUsernameOrEmail(usernameOrEmail);
        if (authUserModel == null) {
            throw new OutingException("username or email not found",HttpStatus.NOT_FOUND);
        }
        authUserModel.setResetId(UUID.randomUUID().toString());
        userRepository.save(authUserModel);
        String uriToBeEmail=resetUri.replace("{userId}", authUserModel.getId()).replace("{resetId}", authUserModel.getResetId());
        //TODO:send email having uriToBeEmail
        String email = authUserModel.getEmail();
        String subject = "Credentials Change Request";
        String message = uriToBeEmail;
        sendEmail(email,subject,message);
        System.out.println(uriToBeEmail);
    }

    @Override
    public void resetPassword(String userId, String resetId, ResetPasswordDto resetPasswordDto) {
        System.out.println(userId);
        if (resetPasswordDto.getNewPassword()==null || resetPasswordDto.getConfirmPassword()==null) {
            throw new OutingException(Constants.UNPROCESSABLE_REQUEST, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        List<String> errors = new ArrayList<>();
        if (!Objects.equals(resetPasswordDto.getNewPassword(), resetPasswordDto.getConfirmPassword())) {
            errors.add(Constants.UNCONFIRMED_PASSWORD);
        }
        if (!errors.isEmpty()) {
            throw new OutingException(errors, HttpStatus.BAD_REQUEST);
        }
        AuthUserModel userInDb = userRepository.findById(userId).orElseThrow(() -> new OutingException(Constants.USER_NOTFOUND, HttpStatus.NOT_FOUND));
        if(!Objects.equals(userInDb.getResetId(), resetId)){
            throw new OutingException(Constants.USER_NOTFOUND,HttpStatus.NOT_FOUND);
        }
        userInDb.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        userInDb.setResetId(null);
        userRepository.save(userInDb);

    }

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void deleteExpiredUsers() {
        userRepository.deleteByRegisterTimeBefore(LocalDateTime.now().minusMinutes(7*24*60));
    }

    @Async
    public void sendEmail(String toEmail,String subject,String message){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom("ashishrefjob@gmail.com");
        javaMailSender.send(mailMessage);
    }

}
