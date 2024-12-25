package com.stock.entity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.cloud.firestore.annotation.PropertyName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails{

	@PropertyName("userId")
	private String userId;

	@PropertyName("name")
	private String name;

	@PropertyName("email")
	private String email;

	@PropertyName("password")
	private String password;

	@PropertyName("incomes")
	private Map<String, Income> incomes;

	@PropertyName("expenses")
	private Map<String, Expense> expenses;
	


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() {
		return this.password;
	}


	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
		
	
}
