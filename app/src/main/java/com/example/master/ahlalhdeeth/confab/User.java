/*
 * Copyright (C) 2011 Paul Bourke <pauldbourke@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.example.master.ahlalhdeeth.confab;

import org.apache.http.protocol.HttpContext;

public class User {
    public String username;
    public String password;
    public String vb_security_token;
    public HttpContext httpContext;
    
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        vb_security_token = new String();
    }

    public User() {
        this.username = new String();
        this.password = new String();
        this.vb_security_token = new String();
    }
}