package synapps.resona.api.member.entity.profile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import synapps.resona.api.member.exception.LanguageException;

public enum Language {
  AFRIKAANS("af", "Afrikaans"),
  ALBANIAN("sq", "Albanian"),
  AMHARIC("am", "Amharic"),
  ARABIC("ar", "Arabic"),
  ARMENIAN("hy", "Armenian"),
  ASSAMESE("as", "Assamese"),
  AYMARA("ay", "Aymara"),
  AZERBAIJANI("az", "Azerbaijani"),
  BAMBARA("bm", "Bambara"),
  BASQUE("eu", "Basque"),
  BELARUSIAN("be", "Belarusian"),
  BENGALI("bn", "Bengali"),
  BHOJPURI("bho", "Bhojpuri"),
  BOSNIAN("bs", "Bosnian"),
  BULGARIAN("bg", "Bulgarian"),
  CATALAN("ca", "Catalan"),
  CEBUANO("ceb", "Cebuano"),
  CHICHEWA("ny", "Chichewa"),
  CHINESE_SIMPLIFIED("zh", "Chinese (Simplified)"),
  CHINESE_SIMPLIFIED_CN("zh-CN", "Chinese (Simplified)"),
  CHINESE_TRADITIONAL("zh-TW", "Chinese (Traditional)"),
  CORSICAN("co", "Corsican"),
  CROATIAN("hr", "Croatian"),
  CZECH("cs", "Czech"),
  DANISH("da", "Danish"),
  DIVEHI("dv", "Divehi"),
  DOGRI("doi", "Dogri"),
  DUTCH("nl", "Dutch"),
  ENGLISH("en", "English"),
  ESPERANTO("eo", "Esperanto"),
  ESTONIAN("et", "Estonian"),
  EWE("ee", "Ewe"),
  FILIPINO("tl", "Filipino"),
  FILIPINO_ALT("fil", "Filipino"),
  FINNISH("fi", "Finnish"),
  FRENCH("fr", "French"),
  FRISIAN("fy", "Frisian"),
  GALICIAN("gl", "Galician"),
  GANDA("lg", "Ganda"),
  GEORGIAN("ka", "Georgian"),
  GERMAN("de", "German"),
  GREEK("el", "Greek"),
  GUARANI("gn", "Guarani"),
  GUJARATI("gu", "Gujarati"),
  HAITIAN_CREOLE("ht", "Haitian Creole"),
  HAUSA("ha", "Hausa"),
  HAWAIIAN("haw", "Hawaiian"),
  HEBREW("he", "Hebrew"),
  HEBREW_ALT("iw", "Hebrew"),
  HINDI("hi", "Hindi"),
  HMONG("hmn", "Hmong"),
  HUNGARIAN("hu", "Hungarian"),
  ICELANDIC("is", "Icelandic"),
  IGBO("ig", "Igbo"),
  ILOKO("ilo", "Iloko"),
  INDONESIAN("id", "Indonesian"),
  IRISH_GAELIC("ga", "Irish Gaelic"),
  ITALIAN("it", "Italian"),
  JAPANESE("ja", "Japanese"),
  JAVANESE("jw", "Javanese"),
  JAVANESE_ALT("jv", "Javanese"),
  KANNADA("kn", "Kannada"),
  KAZAKH("kk", "Kazakh"),
  KHMER("km", "Khmer"),
  KINYARWANDA("rw", "Kinyarwanda"),
  KONKANI("gom", "Konkani"),
  KOREAN("ko", "Korean"),
  KRIO("kri", "Krio"),
  KURDISH_KURMANJI("ku", "Kurdish (Kurmanji)"),
  KURDISH_SORANI("ckb", "Kurdish (Sorani)"),
  KYRGYZ("ky", "Kyrgyz"),
  LAO("lo", "Lao"),
  LATIN("la", "Latin"),
  LATVIAN("lv", "Latvian"),
  LINGALA("ln", "Lingala"),
  LITHUANIAN("lt", "Lithuanian"),
  LUXEMBOURGISH("lb", "Luxembourgish"),
  MACEDONIAN("mk", "Macedonian"),
  MAITHILI("mai", "Maithili"),
  MALAGASY("mg", "Malagasy"),
  MALAY("ms", "Malay"),
  MALAYALAM("ml", "Malayalam"),
  MALTESE("mt", "Maltese"),
  MAORI("mi", "Maori"),
  MARATHI("mr", "Marathi"),
  MEITEILON("mni-Mtei", "Meiteilon (Manipuri)"),
  MIZO("lus", "Mizo"),
  MONGOLIAN("mn", "Mongolian"),
  MYANMAR("my", "Myanmar (Burmese)"),
  NEPALI("ne", "Nepali"),
  NORTHERN_SOTHO("nso", "Northern Sotho"),
  NORWEGIAN("no", "Norwegian"),
  ODIA("or", "Odia (Oriya)"),
  OROMO("om", "Oromo"),
  PASHTO("ps", "Pashto"),
  PERSIAN("fa", "Persian"),
  POLISH("pl", "Polish"),
  PORTUGUESE("pt", "Portuguese"),
  PUNJABI("pa", "Punjabi"),
  QUECHUA("qu", "Quechua"),
  ROMANIAN("ro", "Romanian"),
  RUSSIAN("ru", "Russian"),
  SAMOAN("sm", "Samoan"),
  SANSKRIT("sa", "Sanskrit"),
  SCOTS_GAELIC("gd", "Scots Gaelic"),
  SERBIAN("sr", "Serbian"),
  SESOTHO("st", "Sesotho"),
  SHONA("sn", "Shona"),
  SINDHI("sd", "Sindhi"),
  SINHALA("si", "Sinhala"),
  SLOVAK("sk", "Slovak"),
  SLOVENIAN("sl", "Slovenian"),
  SOMALI("so", "Somali"),
  SPANISH("es", "Spanish"),
  SUNDANESE("su", "Sundanese"),
  SWAHILI("sw", "Swahili"),
  SWEDISH("sv", "Swedish"),
  TAJIK("tg", "Tajik"),
  TAMIL("ta", "Tamil"),
  TATAR("tt", "Tatar"),
  TELUGU("te", "Telugu"),
  THAI("th", "Thai"),
  TIGRINYA("ti", "Tigrinya"),
  TSONGA("ts", "Tsonga"),
  TURKISH("tr", "Turkish"),
  TURKMEN("tk", "Turkmen"),
  TWI("ak", "Twi"),
  UKRAINIAN("uk", "Ukrainian"),
  URDU("ur", "Urdu"),
  UYGHUR("ug", "Uyghur"),
  UZBEK("uz", "Uzbek"),
  VIETNAMESE("vi", "Vietnamese"),
  WELSH("cy", "Welsh"),
  XHOSA("xh", "Xhosa"),
  YIDDISH("yi", "Yiddish"),
  YORUBA("yo", "Yoruba"),
  ZULU("zu", "Zulu"),
  NOT_DEFINED("None", "Not defined");

  private final String code;
  private final String fullName;

  Language(String code, String fullName) {
    this.code = code;
    this.fullName = fullName;
  }


  /**
   * Get Language enum by its code
   *
   * @param code ISO language code
   * @return Language enum or null if not found
   */
  @JsonCreator
  public static Language fromCode(String code) {
    return Arrays.stream(values())
        .filter(language -> language.code.equalsIgnoreCase(code))
        .findFirst()
        .orElseThrow(LanguageException::languageNotFound);
  }


  @JsonValue
  public String getCode() {
    return code;
  }

}
