package com.demoqa.api.models.testdata;

import com.demoqa.api.models.request.AccountModel;

public record AuthorizedUser(String userId, String token, AccountModel credentials) {}
