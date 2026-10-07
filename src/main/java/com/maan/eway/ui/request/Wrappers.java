
package com.maan.eway.ui.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder	
public class Wrappers implements Serializable
{

    private final static long serialVersionUID = 5131620339173976944L;
    public Object Strng;
}
