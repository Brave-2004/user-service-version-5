package dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.FieldError;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDto {
    private int status;
    private String message;
    private List<FieldErrorDto> fieldErrors;
     public ErrorDto(int status,String message){
         this.status = status;
         this.message = message;
     }
}
