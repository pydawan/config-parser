package br.org.configparser;

import static br.org.verify.Verify.isEmptyOrNull;
import static br.org.verify.Verify.isNotNullOrEmpty;
import static br.org.verify.Verify.isNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * <p><strong>ConfigParser</strong> é responsável por manipular arquivos de configuração.</p>
 * <p>
 * Um arquivo de configuração é uma extensão do java.util.Properties permitindo definir seções ou 
 * agrupamentos de itens de configuração.
 * Com isso temos arquivos de propriedades mais legíveis e fáceis de manter.
 * </p>
 * 
 * @author thiago-amm
 * @version v1.0.0 04/09/2017
 * @version v1.0.1 08/09/2017
 * @since v1.0.0
 */
public final class ConfigParser {
   
   private static final File CURRENT_DIR = new File(".");
   
   public static final String PROJECT_DIR = CURRENT_DIR.getAbsolutePath().replace(".", "");
   
   public static final String SRC_DIR = (PROJECT_DIR + "src/").replaceAll("/", File.separator);
   public static final String SRC_MAIN_DIR = (SRC_DIR + "main/").replaceAll("/", File.separator);
   public static final String SRC_MAIN_JAVA_DIR = (SRC_MAIN_DIR + "java/").replaceAll("/", File.separator);
   public static final String SRC_MAIN_RESOURCES_DIR = (SRC_MAIN_DIR + "resources/").replaceAll("/", File.separator);
   
   public static final String SRC_TEST_DIR = (SRC_DIR + "test/").replaceAll("/", File.separator);
   public static final String SRC_TEST_JAVA_DIR = (SRC_TEST_DIR + "java/").replaceAll("/", File.separator);
   public static final String SRC_TEST_RESOURCES_DIR = (SRC_TEST_DIR + "resources/").replaceAll("/", File.separator);
   
   public static final String BUILD_DIR = (PROJECT_DIR + "build/").replaceAll("/", File.separator);
   public static final String BIN_DIR = (PROJECT_DIR + "bin/").replaceAll("/", File.separator);
   
   private File file;
   private File tempFile;
   private Properties properties;
   private Properties tempProperties;
   private InputStream inputStream;
   private InputStream tempInputStream;
   private OutputStream outputStream;
   private OutputStream tempOutputStream;
   private BufferedReader bufferedReader;
   private String line = "";
   private Map<String, Map<String, String>> sections;
   private static final Set<String> EMPTY_SECTIONS = new HashSet<>();
   private Map<String, String> referencedKeys;
   private String currentSection = "";
   private boolean loaded = false;
   
   public static ConfigParser configParser() {
      return new ConfigParser();
   }
   
   /**
    * <p>
    * Carrega um arquivo de configuração com base em seu caminho ou nome, prefixado ou não, pelo contexto de carregamento.
    * O contexto de carregamento admite os valores: <em>classpath</em>, <em>main</em> ou <em>test</em>.
    * </p>
    * 
    * <ul>
    *    <li><strong>classpath</strong> - indica que o contexto é o classpath da aplicação.</li>
    *    <li><strong>main</strong> - indica que o contexto é o diretório src/main/resources.</li>
    *    <li><strong>test</strong> - indica que o contexto é o diretório src/test/resources.</li>
    * </ul>
    * 
    * @param path caminho ou nome do arquivo de configurações.
    */
   public void read(String path) {
      path = path == null ? "" : path;
      if (!path.isEmpty()) {
         properties = new Properties();
         try {
            if (path.startsWith("classpath:")) {
               path = path.replace("classpath:", "");
               file = new File(ConfigParser.class.getClassLoader().getResource(path).getFile());
            } else if (path.startsWith("main:")) {
               path = path.replace("main:", SRC_MAIN_RESOURCES_DIR);
               file = new File(path);
            } else if (path.startsWith("test:")) {
               path = path.replace("test:", SRC_TEST_RESOURCES_DIR);
               file = new File(path);
            } else {
               file = new File(path);
            }
            if (file.exists()) {
               tempFile = new File(System.getProperty("java.io.tmpdir") + File.separator + file.getName());
               if (tempFile.exists()) {
                  tempFile.delete();
               }
               createTempFile();
               inputStream = new FileInputStream(file);
               tempInputStream = new FileInputStream(tempFile);
               tempProperties = new Properties();
               tempProperties.load(tempInputStream);
               outputStream = null;
               tempOutputStream = null;
               loadSections(inputStream);
               System.out.println(sections);
               properties.load(inputStream);
               loaded = true;
            } else {
               throw new IOException(String.format("O caminho: %s não foi encontrado no sistema de arquivos!", path));
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
   
   public void read(File file) {
      if (file != null && file.exists()) {
         properties = new Properties();
         try {
            this.file = file;
            inputStream = new FileInputStream(file);
            properties.load(inputStream);
            loaded = true;
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
   
   public void read(InputStream inputStream) {
      if (inputStream != null) {
         properties = new Properties();
         try {
            properties.load(inputStream);
            this.inputStream = inputStream;
            loaded = true;
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
   
   private void openInputStream() {
      try {
         inputStream = new FileInputStream(file);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
   }
   
   private void openTempInputStream() {
      try {
         tempInputStream = new FileInputStream(tempFile);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
   }
   
   private void openOutputStream() {
      try {
         outputStream = new FileOutputStream(file);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
   }
   
   private void openTempOutputStream() {
      try {
         tempOutputStream = new FileOutputStream(tempFile);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
   }
   
   private void closeInputStream() {
      if (inputStream != null && loaded) {
         try {
            inputStream.close();
            loaded = false;
            inputStream = null;
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
   
   private void closeTempInputStream() {
      if (tempInputStream != null && loaded) {
         try {
            tempInputStream.close();
            loaded = false;
            tempInputStream = null;
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
   
   private void closeOutputStream() {
      if (outputStream != null) {
         try {
            outputStream.close();
            outputStream = null;
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
   
   private void closeTempOutputStream() {
      if (tempOutputStream != null) {
         try {
            tempOutputStream.close();
            tempOutputStream = null;
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
   
   public void close() {
      closeInputStream();
      closeTempInputStream();
      closeOutputStream();
      closeTempOutputStream();
      file = null;
      tempFile = null;
      properties = null;
      tempProperties = null;
   }
   
   public Object get(String key) {
      String value = null;
      key = key == null ? "" : key;
      if (tempProperties != null && loaded && !key.isEmpty()) {
         value = tempProperties.getProperty(key);
      }
      return value;
   }
   
   public Object get(String section, String key) {
      String value = null;
      section = section == null ? "" : section;
      key = key == null ? "" : key;
      if (isConfigLoaded() && !section.isEmpty() && !key.isEmpty()) {
         value = tempProperties.getProperty(section + "." + key);
      }
      return value;
   }
   
   public void set(String key, Object value) {
      key = key == null ? "" : key;
      if (isConfigLoaded() && !key.isEmpty() && value != null) {
         tempProperties.put(key, value);
      }
   }
   
   public void set(String section, String key, Object value) {
      section = section == null ? "" : section;
      key = key == null ? "" : key;
      validateSection(section);
      if (!key.isEmpty() && value != null) {
         if (!hasSection(section)) {
            sections.put(section, new HashMap<>());
         }
         if (section.isEmpty()) {
            sections.get(section).put(key, (String) value);
            tempProperties.put(key, value);
         } else {
            sections.get(section).put(section + "." + key, (String) value);
            tempProperties.put(section + "." + key, value);
         }
      }
   }
   
   private void validateSection(String section) {
      // TODO - fazer logging das exceções lançadas.
      if (configNotLoaded()) {
         throw new IllegalStateException("ATENÇÃO: O arquivo de configurações não foi carregado!");
      }
      if (isNull(section)) {
         throw new IllegalArgumentException("ATENÇÃO: A seção não pode ser nula!");
      }
   }
   
   private void validateOption(String option) {
      // TODO - fazer logging das exceções lançadas.
      if (configNotLoaded()) {
         throw new IllegalStateException("ATENÇÃO: O arquivo de configurações não foi carregado!");
      }
      if (isEmptyOrNull(option)) {
         throw new IllegalArgumentException("ATENÇÃO: A seção não pode ser vazia ou nula!");
      }
   }
   
   public boolean hasSection(String section) {
      validateSection(section);
      return sections.containsKey(section);
   }
   
   public void addSection(String section) {
      validateSection(section);
      if (!hasSection(section)) {
         sections.put(section, new HashMap<>());
      } else {
         throw new IllegalArgumentException("ATENÇÃO: A seção informada já existe!");
      }
      sections.put(section, new HashMap<>());
   }
   
   public boolean removeSection(String section) {
      boolean removeSection = false;
      validateSection(section);
      if (hasSection(section)) {
         sections.remove(section);
         removeSection = true; 
      }
      return removeSection;
   }
   
   public Set<String> getSections() {
      if (isConfigLoaded()) {
         return sections.keySet();
      }
      return EMPTY_SECTIONS;
   }
   
   public Set<String> sections() {
      return getSections();
   }
   
   public Set<Entry<String, String>> options(String section) {
      Set<Entry<String, String>> options = null;
      validateSection(section);
      if (hasSection(section)) {
         options = sections.get(section).entrySet();
      }
      return options;
   }
   
   public boolean hasOption(String section, String option) {
      boolean hasOption = false;
      validateSection(section);
      validateOption(option);
      if (hasSection(section) && sections.get(section).containsKey(section + "." + option)) {
         hasOption = true;
      }
      return hasOption;
   }
   
   public boolean removeOption(String option) {
      boolean removeOption = false;
      return removeOption;
   }
   
   public Set<Entry<String,String>> items(String section) {
      Set<Entry<String, String>> items = null;
      validateSection(section);
      if (hasSection(section)) {
         items = sections.get(section).entrySet();
      }
      return items;
   }
   
   @SuppressWarnings("unused")
   private void _write() {
      try {
         if (configLoaded()) {
            inputStream.close();
            openOutputStream(); // cria fluxo de saída.
            properties.store(outputStream, ""); // armazena os dados.
            closeOutputStream();
            openInputStream();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   public void write() {
      try {
         if (configLoaded()) {
            closeTempInputStream();
            openTempOutputStream();
//            tempProperties.store(tempOutputStream, "config-parser file");
            tempProperties.store(tempOutputStream, null);
            closeTempOutputStream();
            openTempInputStream();
            loaded = true;
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   public void write(OutputStream outputStream) {
      this.outputStream = outputStream;
      write();
   }
   
   public boolean isConfigLoaded() {
      return properties != null && loaded;
   }
   
   public boolean configLoaded() {
      return isConfigLoaded();
   }
   
   public boolean isConfigNotLoaded() {
      return !isConfigLoaded();
   }
   
   public boolean configNotLoaded() {
      return isConfigNotLoaded();
   }
   
   private void loadSections(InputStream inputStream) {
      try {
         sections = new HashMap<>();
         sections.put("", new HashMap<>());
         referencedKeys = new HashMap<>();
         bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
         while ((line = bufferedReader.readLine()) != null) {
            if (line.matches("^\\[\\w+]$")) {
               currentSection = line;
               currentSection = currentSection.replace("[", "");
               currentSection = currentSection.replace("]", "");
               sections.put(currentSection, new HashMap<>());
            } else {
               if (line.matches("^$")) {
                  currentSection = "";
               } else if (line.matches("^.*=.*$")) {
                  String key = line.split("=")[0];
                  String value = line.split("=")[1];
                  key = currentSection.isEmpty() ? key : currentSection + "." + key;
                  sections.get(currentSection).put(key, value);
                  tempProperties.put(key, value);
                  if (isReferencedKey(value)) {
                     value = value.substring(value.indexOf("${") + 2, value.lastIndexOf("}"));
                     referencedKeys.put(key, value);
                  }
               }
            }
         }
         if (!referencedKeys.isEmpty()) {
            String key = "";
            String value = "";
            String referencedKey = "";
            String referencedValue = "";
            for (Entry<String, String> entry : referencedKeys.entrySet()) {
               key = entry.getKey();
               referencedKey = entry.getValue();
               value = tempProperties.getProperty(key);
               referencedValue = tempProperties.getProperty(referencedKey);
               // TODO - criar método recursivo.
               if (isReferencedKey(referencedValue)) {
                  referencedValue = referencedValue.substring(
                     referencedValue.indexOf("${") + 2, 
                     referencedValue.lastIndexOf("}")
                  );
                  referencedValue = tempProperties.getProperty(referencedValue);
               }
               value = value.replace(String.format("${%s}", referencedKey), referencedValue);
               tempProperties.setProperty(key, value);
            }
         }
         writeTempFile();
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   private boolean isReferencedKey(String value) {
      boolean isReferencedKey = false;
      if (isNotNullOrEmpty(value)) {
         if (value.matches("^\\$\\{.*\\}.*")) {
            isReferencedKey = true;
         }
      }
      return isReferencedKey;
   }
   
   private void createTempFile() {
      tempFile = new File(System.getProperty("java.io.tmpdir") + File.separator + file.getName());
      if (tempFile.exists()) {
         tempFile.delete();
      }
      try {
         tempFile.createNewFile();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   private void writeTempFile() throws IOException {
      if (isConfigLoaded()) {
         openTempOutputStream();
         tempProperties.store(tempOutputStream, "");
         closeTempOutputStream();
         closeTempInputStream();
      }
   }
   
}
