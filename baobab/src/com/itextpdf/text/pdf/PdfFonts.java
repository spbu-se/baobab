package com.itextpdf.text.pdf;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;

public class PdfFonts {
  public static Font BIG_FONT;
  public static Font MEDIUM_FONT;
  public static Font SMALL_FONT;

  public static Font CONSULT_LABEL;
  public static Font TEACHER_LABEL;
  
  static {
    try {
      BaseColor grey = new BaseColor(0x666666);
      byte[] fontBytes = Resources.toByteArray(Resources.getResource("LiberationSans-Regular.ttf"));
      TrueTypeFontUnicode ttfFont = new TrueTypeFontUnicode("LiberationSans-Regular.ttf", BaseFont.IDENTITY_H, true, fontBytes, false);
      BIG_FONT = new Font(ttfFont, 30, Font.NORMAL, new BaseColor(0, 0, 0));
      MEDIUM_FONT = new Font(ttfFont, 16, Font.NORMAL, new BaseColor(0, 0, 0));
      SMALL_FONT = new Font(ttfFont, 10, Font.NORMAL, new BaseColor(0, 0, 0));
      
      CONSULT_LABEL = new Font(ttfFont, 10, Font.NORMAL, grey);
      TEACHER_LABEL = new Font(ttfFont, 16, Font.NORMAL, grey);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (DocumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static Font getFont(String style) {
    return null;
  }
}
