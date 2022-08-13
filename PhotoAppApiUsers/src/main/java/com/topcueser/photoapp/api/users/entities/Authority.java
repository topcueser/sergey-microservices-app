package com.topcueser.photoapp.api.users.data;

import javax.persistence.Id;

public class Authority {

    @Id
    @Gen
    private long id;

    private String authorityName;
}
