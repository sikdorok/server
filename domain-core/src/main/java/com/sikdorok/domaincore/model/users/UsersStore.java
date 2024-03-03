package com.sikdorok.domaincore.model.users;

public interface UsersStore {

    Users save(Users users);

    void delete(Users users);

}
