package com.synapps.resona.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.synapps.resona.error.exception.LanguageException;
import java.util.Arrays;

public enum Language {

  af("af", "Afrikaans"),
  sq("sq", "Albanian"),
  am("am", "Amharic"),
  ar("ar", "Arabic"),
  hy("hy", "Armenian"),
  as("as", "Assamese"),
  ay("ay", "Aymara"),
  az("az", "Azerbaijani"),
  bm("bm", "Bambara"),
  eu("eu", "Basque"),
  be("be", "Belarusian"),
  bn("bn", "Bengali"),
  bho("bho", "Bhojpuri"),
  bs("bs", "Bosnian"),
  bg("bg", "Bulgarian"),
  ca("ca", "Catalan"),
  ceb("ceb", "Cebuano"),
  ny("ny", "Chichewa"),
  zh("zh", "Chinese (Simplified)"),
  zh_CN("zh-CN", "Chinese (Simplified)"),
  zh_TW("zh-TW", "Chinese (Traditional)"),
  co("co", "Corsican"),
  hr("hr", "Croatian"),
  cs("cs", "Czech"),
  da("da", "Danish"),
  dv("dv", "Divehi"),
  doi("doi", "Dogri"),
  nl("nl", "Dutch"),
  en("en", "English"),
  eo("eo", "Esperanto"),
  et("et", "Estonian"),
  ee("ee", "Ewe"),
  tl("tl", "Filipino"),
  fil("fil", "Filipino"),
  fi("fi", "Finnish"),
  fr("fr", "French"),
  fy("fy", "Frisian"),
  gl("gl", "Galician"),
  lg("lg", "Ganda"),
  ka("ka", "Georgian"),
  de("de", "German"),
  el("el", "Greek"),
  gn("gn", "Guarani"),
  gu("gu", "Gujarati"),
  ht("ht", "Haitian Creole"),
  ha("ha", "Hausa"),
  haw("haw", "Hawaiian"),
  he("he", "Hebrew"),
  iw("iw", "Hebrew"),
  hi("hi", "Hindi"),
  hmn("hmn", "Hmong"),
  hu("hu", "Hungarian"),
  is("is", "Icelandic"),
  ig("ig", "Igbo"),
  ilo("ilo", "Iloko"),
  id("id", "Indonesian"),
  ga("ga", "Irish Gaelic"),
  it("it", "Italian"),
  ja("ja", "Japanese"),
  jw("jw", "Javanese"),
  jv("jv", "Javanese"),
  kn("kn", "Kannada"),
  kk("kk", "Kazakh"),
  km("km", "Khmer"),
  rw("rw", "Kinyarwanda"),
  gom("gom", "Konkani"),
  ko("ko", "Korean"),
  kri("kri", "Krio"),
  ku("ku", "Kurdish (Kurmanji)"),
  ckb("ckb", "Kurdish (Sorani)"),
  ky("ky", "Kyrgyz"),
  lo("lo", "Lao"),
  la("la", "Latin"),
  lv("lv", "Latvian"),
  ln("ln", "Lingala"),
  lt("lt", "Lithuanian"),
  lb("lb", "Luxembourgish"),
  mk("mk", "Macedonian"),
  mai("mai", "Maithili"),
  mg("mg", "Malagasy"),
  ms("ms", "Malay"),
  ml("ml", "Malayalam"),
  mt("mt", "Maltese"),
  mi("mi", "Maori"),
  mr("mr", "Marathi"),
  mni_Mtei("mni-Mtei", "Meiteilon (Manipuri)"),
  lus("lus", "Mizo"),
  mn("mn", "Mongolian"),
  my("my", "Myanmar (Burmese)"),
  ne("ne", "Nepali"),
  nso("nso", "Northern Sotho"),
  no("no", "Norwegian"),
  or("or", "Odia (Oriya)"),
  om("om", "Oromo"),
  ps("ps", "Pashto"),
  fa("fa", "Persian"),
  pl("pl", "Polish"),
  pt("pt", "Portuguese"),
  pa("pa", "Punjabi"),
  qu("qu", "Quechua"),
  ro("ro", "Romanian"),
  ru("ru", "Russian"),
  sm("sm", "Samoan"),
  sa("sa", "Sanskrit"),
  gd("gd", "Scots Gaelic"),
  sr("sr", "Serbian"),
  st("st", "Sesotho"),
  sn("sn", "Shona"),
  sd("sd", "Sindhi"),
  si("si", "Sinhala"),
  sk("sk", "Slovak"),
  sl("sl", "Slovenian"),
  so("so", "Somali"),
  es("es", "Spanish"),
  su("su", "Sundanese"),
  sw("sw", "Swahili"),
  sv("sv", "Swedish"),
  tg("tg", "Tajik"),
  ta("ta", "Tamil"),
  tt("tt", "Tatar"),
  te("te", "Telugu"),
  th("th", "Thai"),
  ti("ti", "Tigrinya"),
  ts("ts", "Tsonga"),
  tr("tr", "Turkish"),
  tk("tk", "Turkmen"),
  ak("ak", "Twi"),
  uk("uk", "Ukrainian"),
  ur("ur", "Urdu"),
  ug("ug", "Uyghur"),
  uz("uz", "Uzbek"),
  vi("vi", "Vietnamese"),
  cy("cy", "Welsh"),
  xh("xh", "Xhosa"),
  yi("yi", "Yiddish"),
  yo("yo", "Yoruba"),
  zu("zu", "Zulu"),
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
   * @param givenCode ISO language code
   * @return Language enum or null if not found
   */
  @JsonCreator
  public static Language fromCode(String givenCode) {
    return Arrays.stream(values())
        .filter(language -> language.code.equalsIgnoreCase(givenCode))
        .findFirst()
        .orElseThrow(LanguageException::languageNotFound);
  }

  /**
   * Finds a Language enum by its declared constant name.
   * For example, Language.of("zh_CN") returns Language.zh_CN
   *
   * @param s the name of the constant to return
   * @return the enum constant with the specified name
   */
  public static Language of(String s) {
    return Arrays.stream(Language.values())
        .filter(r -> r.name().equals(s)) //
        .findAny().orElse(NOT_DEFINED);
  }

  @JsonValue
  public String getCode() {
    return code;
  }
}