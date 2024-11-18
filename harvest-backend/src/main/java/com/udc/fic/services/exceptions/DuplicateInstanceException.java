package com.udc.fic.services.exceptions;

public class DuplicateInstanceException extends InstanceException {


    public DuplicateInstanceException(String name, Object key) {
        super(name, key);
    }


}
