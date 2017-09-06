package br.org.configparser;

/**
 * @author thiago-amm
 * @version v1.0.0 05/09/2017
 * @since v1.0.0
 *
 */
public class ParsingException extends RuntimeException {
   
   private static final long serialVersionUID = 1L;
   
   public ParsingException() {}
   
   public ParsingException(String message) {
      super(message);
   }
   
   public ParsingException(Throwable cause) {
      super(cause);
   }
   
   public ParsingException(String message, Throwable cause) {
      super(message, cause);
   }
   
}
