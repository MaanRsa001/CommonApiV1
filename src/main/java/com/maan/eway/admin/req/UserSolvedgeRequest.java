package com.maan.eway.admin.req;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class UserSolvedgeRequest {
	 @NotBlank(message = "Full name is required")
	    private String fullName;

	    @Email(message = "Invalid email")
	    private String email;

	    @NotBlank(message = "Password is required")
	    private String password;

	    @NotBlank(message = "Phone is required")
	    private String phone;

	    private String address;

	    private Role role;
	    
	    private String reasonForRegistration;
	    
	    private String currency;
}
