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
import me.peihao.autoInvest.constant.WeekDayEnum;
import me.peihao.autoInvest.constant.WhenDuplicateEnum;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WhenDuplicate.WorkdayValidator.class)
@Documented
public @interface WhenDuplicate {
  String message() default "Not allowed when duplicate type";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class WorkdayValidator implements ConstraintValidator<WhenDuplicate, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context){
      WhenDuplicateEnum.forValue(value);
      return true;
    }
  }
}
