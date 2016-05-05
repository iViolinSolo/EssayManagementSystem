/**
 * 
 */
package com.eva.me.dao;

import java.util.List;

import com.eva.me.model.User;

/**
 * @author phoen_000
 *
 */
public interface UserDAO {
	/**
	 * list all users
	 * @return
	 */
	public List<User> list(); 
}