package br.org.configparser;

/**
 * @author thiago-amm
 * @version v1.0.0 05/09/2017
 * @since v1.0.0
 *
 */
public class NoOptionException extends RuntimeException {
   
   private static final long serialVersionUID = 1L;
   
   public NoOptionException() {}
   
   public NoOptionException(String message) {
      super(message);
   }
   
   public NoOptionException(Throwable cause) {
      super(cause);
   }
   
   public NoOptionException(String message, Throwable cause) {
      super(message, cause);
   }
   
}
