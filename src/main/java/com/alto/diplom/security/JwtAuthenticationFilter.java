package com.alto.diplom.security;

import com.alto.diplom.entity.core.Company;
import com.alto.diplom.entity.core.User;
import io.jmix.core.Metadata;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private final JwtUtil jwtUtil;
//    private final Metadata metadata; // Системный бин Jmix
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String authHeader = request.getHeader("TOKEN");
//        String token = jwtUtil.extractTokenFromHeader(authHeader);
//
//        if (token != null && jwtUtil.validateToken(token)) {
//            // Извлекаем данные
//            UUID userId = jwtUtil.getUserId(token);
//            Long companyId = jwtUtil.getCompanyId(token);
//            String username = jwtUtil.getUsername(token);
//
//            // 1. Создаем объект User (сущность)
//            // Мы не грузим его из БД каждый раз ради скорости, но заполняем ID и Company
//            User user = metadata.create(User.class);
//            user.setId(userId);
//            user.setUsername(username);
//
//            // Важно: создаем "пустышку" компании с нужным ID
//            Company company = metadata.create(Company.class);
//            company.setId(companyId);
//            user.setCompany(company);
//
//            // 2. Формируем Authentication
//            // JmixRowLevelPolicyExtractor будет искать поле 'company' именно здесь
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                    user, null, Collections.emptyList()); // authorities добавим позже
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}