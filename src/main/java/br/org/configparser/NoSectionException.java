package br.org.configparser;

/**
 * @author thiago-amm
 * @version v1.0.0 05/09/2017
 * @since v1.0.0
 *
 */
public class NoSectionException extends RuntimeException {
   
   private static final long serialVersionUID = 1L;
   
   public NoSectionException() {}
   
   public NoSectionException(String message) {
      super(message);
   }
   
   public NoSectionException(Throwable cause) {
      super(cause);
   }
   
   public NoSectionException(String message, Throwable cause) {
      super(message, cause);
   }
   
}
