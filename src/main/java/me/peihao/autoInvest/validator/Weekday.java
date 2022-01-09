package me.peihao.autoInvest.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import me.peihao.autoInvest.constant.WeekDayEnum;

// TODO: What is target & retention use for
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Weekday.WorkdayValidator.class)
@Documented
public @interface Weekday {

    String message() default "Not allowed workday type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class WorkdayValidator implements ConstraintValidator<Weekday, String>{

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context){
            WeekDayEnum.forValue(value);
            return true;
        }
    }

}
