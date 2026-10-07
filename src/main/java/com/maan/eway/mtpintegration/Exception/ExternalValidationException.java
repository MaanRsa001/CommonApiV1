package com.maan.eway.mtpintegration.Exception;

import java.util.List;
import com.maan.eway.error.Error;

public class ExternalValidationException extends RuntimeException {

    private final List<Error> errors;

    public ExternalValidationException(List<Error> errors) {
        super("External vehicle validation failed");
        this.errors = errors;
    }

    public List<Error> getErrors() {
        return errors;
    }
}
