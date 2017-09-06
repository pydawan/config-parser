package br.org.configparser;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author thiago-amm
 * @version v1.0.0 04/09/2017
 * @since v1.0.0
 */
public class ConfigParserTest {
   
   @Test
   public void testLeituraArquivo() {
      ConfigParser config = ConfigParser.configParser();
      config.read("test:database.properties");
      System.out.println(config.get("dev", "db.engine"));
      System.out.println(config.get("test", "db.engine"));
      System.out.println(config.get("stage", "db.engine"));
      System.out.println(config.get("prod", "db.engine"));
   }
   
   @Ignore
   @Test
   public void testSave() {
      ConfigParser cfg = ConfigParser.configParser();
      cfg.set("pool", "engine", "hikari");
      cfg.write();
   }
   
   @Test
   public void testSection() {
      ConfigParser cfg = ConfigParser.configParser();
      cfg.read("test:database.properties");
      cfg.sections().forEach(System.out::println);
      cfg.options("dev").forEach(System.out::println);
      System.out.println(cfg.hasOption("dev", "db.engine"));
      cfg.set("francis", "nome", "Francisco Modesto Gomes");
      cfg.write();
      Object francisNome = cfg.get("francis", "nome");
      System.out.println(francisNome);
   }
   
}
