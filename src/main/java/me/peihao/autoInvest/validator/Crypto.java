package me.peihao.autoInvest.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Crypto.CryptoValidator.class)
@Documented
public @interface Crypto {

    String message() default "Fail to find crypto pair";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class CryptoValidator implements ConstraintValidator<Crypto, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context){
            // TODO: Check all charactor is crypto or not
            // TODO: Call Binance API to verify
            return true;
        }
    }

}
