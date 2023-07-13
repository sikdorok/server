package com.ddd.chulsi.infrastructure.mail;

import com.ddd.chulsi.domainCore.model.users.Users;

import java.util.List;

public interface MailService {

    boolean sendMail(List<Users> receiveList);

}
