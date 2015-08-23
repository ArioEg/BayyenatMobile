package ir.najmossagheb.core.persian;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * Created by r.kiani on 05/21/2015.
 */
public class ConvertFarsiStyle {

    enum CHAR_TYPE{
        ISOLATED, MIDDLE, FINAL, INITIAL
    }

    private String Text="";
    private String CheckChars1=" ‌، ▐‬:/_'.,،»«؛0123456789-*/+=)(*&%$#@!{}[]<>\\\"'?|abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZدذرزژواآ \r\n";
    private String CheckChars2=" ‌، ▐‬:/_'.,،»«؛0123456789-*/+=)(*&%$#@!{}[]<>\\\"'?|abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ\r\n ًٌٍَُِّ";
    private String CheckChars3="،  !@#$%&*+_-=/?\\.,،\"';:| ()[]><";
    private String LTRchars=" ‌، ▐!0123456789\"'@#%&.,_*/?-+=abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'@#$*:؟\\;|ًٌٍَُِّ ◙";
    private String ENchars="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String CheckChars4="◙ًٌٍَُِّ";
    private int EN=0;
    private int Skiper=0;
    private int Skiper2=0;

    private int getIndex(char chr)
    {
        int[] iso2={
                1570,1575,1576,1662,1578,1579,1580,1670,
                1581,1582,1583,1584,1585,1586,1688,1587,
                1588,1589,1590,1591,1592,1593,1594,1601,
                1602,1705,1603,1711,1604,1605,1606,1608,1607,1726,
                1740,1610,1571,1569,1574,
                ')','(',']','[','}','{','>','<'
        };

        for (int i=0; i<iso2.length; i++)
        {
            if(iso2[i] == (int)chr) return i;
        }
        return -1;
    }

    private char returnChars(char chr,CHAR_TYPE type) {
        char returnChar=0;
        String Isolated="ﺁﺍﺏﭖﺕﺙﺝﭺﺡﺥﺩﺫﺭﺯﮊﺱﺵﺹﺽﻁﻅﻉﻍﻑﻕﮎﮎﮒﻝﻡﻥﻭﻩﻩﻯﻯﺃﺉﺀ)(][}{><";
        String Middle= "ﺁﺎﺒﭙﺘﺜﺠﭽﺤﺨﺪﺬﺮﺰﮋﺴﺸﺼﻀﻄﻈﻌﻐﻔﻘﻜﻜﮕﻠﻤﻨﻮﻬﻬﻴﻴﺄﺌﺌ";
        String Final="ﺁﺎﺐﭗﺖﺚﺞﭻﺢﺦﺪﺬﺮﺰﮋﺲﺶﺺﺾﻂﻆﻊﻎﻒﻖﮏﮏﮓﻞﻢﻦﻮﻪﻪﻰﻰﺄﺊﺀ";
        String Initial="ﺁﺍﺑﭘﺗﺛﺟﭼﺣﺧﺩﺫﺭﺯﮊﺳﺷﺻﺿﻃﻇﻋﻏﻓﻗﻛﻛﮔﻟﻣﻧﻭﻫﻫﻳﻳﺃﺋﺋ";

        switch (type) {
            case ISOLATED:
                if (this.LTRchars.indexOf(chr) != -1) {
                    returnChar=chr;
                } else {
                    //chr is Isolated!
                    Log.d("CHAR","iso="+String.valueOf(chr));
                    returnChar= Isolated.charAt(getIndex(chr));
                }
                return returnChar;
            case FINAL:
                returnChar=Final.charAt(getIndex(chr));
                Log.d("CHAR","fin="+String.valueOf(chr));
                return returnChar;
            case INITIAL:
                returnChar=Initial.charAt(getIndex(chr));
                Log.d("CHAR","ini="+String.valueOf(chr));
                return returnChar;
            case MIDDLE:
                returnChar=Middle.charAt(getIndex(chr));
                Log.d("CHAR","mid="+String.valueOf(chr));
                return returnChar;
        }

        return returnChar;
    }

    private void CreateText(String Text) {
        String Chars;
        String strText = "";
        String ReturnText = "";
        boolean canEn=true;
        int UD;
        int AF;
        Chars = Text;
        for (int i = 0; i<Text.length(); i++) {
            canEn = true;
            if (this.CheckChars3.indexOf(Chars.charAt(i))!=-1 && this.Skiper == 0) {
                if (this.CheckChars3.indexOf(Chars.charAt(i-1))==-1 && this.ENchars.indexOf(Chars.charAt(i-1))==-1 || i-1 >= 0) {
                    if (this.CheckChars3.indexOf(Chars.charAt(i+1))!=-1) {
                        for (int X=0; X<Chars.length(); X++) {
                            if (this.CheckChars3.indexOf(Chars.charAt(X+i))!=-1) {
                                strText=this.returnChars(Chars.charAt(X+i),CHAR_TYPE.ISOLATED)+strText;
                            } else {
                                i += X-1;
                                X = Chars.length();
                            }
                        }
                    } else {
                        strText=this.returnChars(Chars.charAt(i),CHAR_TYPE.ISOLATED)+strText;
                    }
                    canEn=false;
                }
                if (this.ENchars.indexOf(Chars.charAt(i-1))!=-1) {
                    if (this.CheckChars3.indexOf(Chars.charAt(i+1))!=-1) {
                        for (int j = 0; j<Chars.length(); j++) {
                            if (this.ENchars.indexOf(Chars.charAt(j+(i)))!=-1) {
                                this.Skiper = j;
                                j = Chars.length();
                                canEn = true;
                            } else if (this.CheckChars3.indexOf(Chars.charAt(j+(i)))==-1) {
                                this.Skiper2 = j;
                                j = Chars.length();
                                canEn = false;
                            }
                        }
                        if (this.Skiper2!=0) {
                            for (int j2 = 0; j2<this.Skiper2; j2++) {
                                strText=this.returnChars(Chars.charAt(j2+i),CHAR_TYPE.ISOLATED)+strText;
                            }
                            i += this.Skiper2-1;
                            this.Skiper2 = 0;
                        }
                    } else if (this.ENchars.indexOf(Chars.charAt(i+1))==-1) {
                        strText=this.returnChars(Chars.charAt(i),CHAR_TYPE.ISOLATED)+strText;
                        canEn = false;
                    }
                }
            }
            if (this.LTRchars.indexOf(Chars.charAt(i)) != -1 && canEn) {
                if (this.Skiper!=0) {
                    this.Skiper--;
                }
                this.EN++;
                String Achars = strText.substring(0, this.EN-1);
                String Bchars = strText.substring(this.EN-1, strText.length());
                Achars += this.returnChars(Chars.charAt(i), CHAR_TYPE.ISOLATED);
                strText= Achars+Bchars;
            } else if (canEn) {
                this.EN = 0;
                UD=1;
                AF=1;
                if (i > 0 && this.CheckChars4.indexOf(Chars.charAt(i-1)) != -1) {
                    UD=2;
                }
                if (i+1 < Chars.length() && this.CheckChars4.indexOf(Chars.charAt(i+1)) != -1) {
                    AF=2;
                }
                if (i == 0 || (i > 0 && this.CheckChars1.indexOf(Chars.charAt(i-UD)) != -1)) {
                    if ((i+AF < Chars.length() && this.CheckChars2.indexOf(Chars.charAt(i+AF)) != -1) || i+AF >= Chars.length()) {
                        strText = this.returnChars(Chars.charAt(i), CHAR_TYPE.ISOLATED)+strText;
                    } else {
                        strText = this.returnChars(Chars.charAt(i), CHAR_TYPE.INITIAL)+strText;
                    }
                } else if ((i+AF < Chars.length() && this.CheckChars2.indexOf(Chars.charAt(i+AF)) != -1) || i+AF >= Chars.length()) {
                    strText = this.returnChars(Chars.charAt(i), CHAR_TYPE.FINAL)+"‍"+strText;
                } else {
                    strText = this.returnChars(Chars.charAt(i), CHAR_TYPE.MIDDLE)+"‍"+strText;
                }
            }
        }
        ReturnText += strText;
        this.Text = ReturnText;
    }
    public String convertText(String Text) {
        String tmp="";
        for (int i = 1; i <= Text.length(); i++) {
            tmp = tmp + Text.charAt(Text.length()-i);
        }

        this.CreateText(Text);
        return this.Text;
    }
}
