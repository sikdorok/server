package com.sikdorok.appapi.infrastructure.mail;

import com.sikdorok.domaincore.model.users.Users;

import java.util.List;

public interface MailService {

    boolean sendMail(List<Users> receiveList, String redisCode);

}
