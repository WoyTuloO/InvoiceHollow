package com.woytuloo.accountingapp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface DateTimeProvider {
    LocalDate today();
    LocalDateTime now();
}
