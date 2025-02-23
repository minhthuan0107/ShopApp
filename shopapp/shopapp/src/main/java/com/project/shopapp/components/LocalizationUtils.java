package com.project.shopapp.components;

import com.project.shopapp.ultis.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class LocalizationUtils {
    private final LocaleResolver localeResolver;
    private  final MessageSource messageSource;
    public String getLocalizedMessage(String messageKey, Object ... params){//spead operator
        HttpServletRequest request = WebUtils.getCurrentRequest();
        Locale locale = localeResolver.resolveLocale(request);
        return messageSource.getMessage(messageKey,params,locale);
    }
}
