package net.engine.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextParser
{
  private StringZero text;
  private int position;
  private boolean outsideText;
  private boolean cppStyleCommentsAsWhiteSpace;
  private List<Integer> mcPositions;

  public TextParser(File file) throws IOException
  {
    FileReader fileReader = new FileReader(file);
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    text = new StringZero((int) file.length() + 1);
    bufferedReader.read(text.getValues());
    text.setEnd((int) file.length());
    intiailise();
  }

  public TextParser(char[] text)
  {
    cppStyleCommentsAsWhiteSpace = true;
    this.text = new StringZero(text);
    intiailise();
  }

  public TextParser(String s)
  {
    text = new StringZero(s);
    intiailise();
  }

  public void setCppStyleCommentsAsWhiteSpace(boolean cppStyleCommentsAsWhiteSpace)
  {
    this.cppStyleCommentsAsWhiteSpace = cppStyleCommentsAsWhiteSpace;
  }

  private void intiailise()
  {
    cppStyleCommentsAsWhiteSpace = true;
    position = 0;
    mcPositions = new ArrayList<Integer>();
  }

  private int textLength()
  {
    return text.length();
  }

  public void stepRight()
  {
    //Can only move right if we are not sitting in the last character.
    if (position <= (textLength()))
    {
      position++;
    }
    testEnd();
  }

  public void stepLeft()
  {
    //Can only move right if we are not sitting in the last character.
    if (position >= 0)
    {
      position--;
    }
    testEnd();
  }

  public void testEnd()
  {
    if ((position >= 0) && (position < textLength()))
    {
      outsideText = false;
    }
    else
    {
      outsideText = true;
    }
  }

  public void skipWhiteSpace()
  {
    char cCurrent;

    for (; ;)
    {
      if (outsideText)
      {
        return;
      }

      cCurrent = text.get(position);

      //Nice clean white space...
      if ((cCurrent == ' ') || (cCurrent == '\r') || (cCurrent == '\n') || (cCurrent == '\t'))
      {
        stepRight();
      }

      //Possibly nasty comments...
      else if ((cCurrent == '/') && (cppStyleCommentsAsWhiteSpace))
      {
        stepRight();

        if (!outsideText)
        {
          cCurrent = text.get(position);
          if (cCurrent == '*')
          {
            //Put the parser back where it was.
            stepLeft();
            SkipCStyleComment();
          }
          else if (cCurrent == '/')
          {
            //Put the parser back where it was.
            stepLeft();
            SkipCPPStyleComment();
          }
          else
          {
            //Was something other than white-space starting with /
            stepLeft();
            break;
          }
        }
      }
      else
      {
        //Was not white-space at all.
        break;
      }
    }
  }

  void SkipCStyleComment()
  {
    char cCurrent;
    int iDepth;

    iDepth = 0;

    pushPosition();
    for (; ;)
    {
      if (outsideText)
      {
        passPosition();
        return;
      }

      cCurrent = text.get(position);
      if (cCurrent == '/')
      {
        stepRight();
        if (!outsideText)
        {
          cCurrent = text.get(position);
          if (cCurrent == '*')
          {
            iDepth++;
          }
          else
          {
            //Wasn't a comment start... step back.
            stepLeft();
          }
        }
        else
        {
          passPosition();
          return;
        }
      }
      else if (cCurrent == '*')
      {
        stepRight();
        if (!outsideText)
        {
          cCurrent = text.get(position);
          if (cCurrent == '/')
          {
            iDepth--;
          }
          else
          {
            //Wasn't the end of a comment... step back...
            stepLeft();
          }
        }
        else
        {
          passPosition();
          return;
        }
      }

      if (iDepth == 0)
      {
        //No more nested comments...  bail..
        return;
      }
      stepRight();
    }
  }

  void SkipCPPStyleComment()
  {
    char cCurrent;

    if (outsideText)
    {
      return;
    }

    pushPosition();
    cCurrent = text.get(position);

    if (cCurrent == '/')
    {
      stepRight();
      if (!outsideText)
      {
        cCurrent = text.get(position);
        if (cCurrent == '/')
        {
          for (; ;)
          {
            stepRight();
            if (!outsideText)
            {
              cCurrent = text.get(position);

              if (cCurrent == '\r')
              {
                stepRight();
                if (!outsideText)
                {
                  cCurrent = text.get(position);
                  if (cCurrent == '\n')
                  {
                    //This is the end of the line and the end of the comment.
                    stepRight();
                    passPosition();
                    return;
                  }
                  else
                  {
                    passPosition();
                    return;
                  }
                }
                else
                {
                  passPosition();
                  return;
                }
              }
              else if (cCurrent == '\n')
              {
                //This is the end of the line and the end of the comment.
                stepRight();
                passPosition();
                return;
              }
            }
            else
            {
              passPosition();
              return;
            }
          }
        }
        else
        {
          popPosition();
          return;
        }
      }
      else
      {
        //Wasn't a comment.
        stepLeft();
        return;
      }
    }
    popPosition();
    return;
  }

  public ParseResult getExactCharacter(char c)
  {
    return getExactCharacter(c, true);
  }

  public ParseResult getExactCharacter(char c, boolean bSkipWhiteSpace)
  {
    if (bSkipWhiteSpace)
    {
      skipWhiteSpace();
    }
    if (!outsideText)
    {
      if (text.get(position) == c)
      {
        stepRight();
        return ParseResult.TRUE;
      }
      return ParseResult.FALSE;
    }
    else
    {
      return ParseResult.ERROR;
    }
  }

  public ParseResult getCharacter(StringZero pc)
  {
    if (!outsideText)
    {
      pc.set(0, text.get(position));
      stepRight();
      return ParseResult.TRUE;
    }
    else
    {
      return ParseResult.ERROR;
    }
  }

  public ParseResult getIdentifierCharacter(CharPointer pc, boolean bFirst)
  {
    char cCurrent;

    if (!outsideText)
    {
      cCurrent = text.get(position);
      pc.value = cCurrent;
      //The first character of an identifier must be one of these...
      if (((cCurrent >= 'a') && (cCurrent <= 'z')) || ((cCurrent >= 'A') && (cCurrent <= 'Z')) || (cCurrent == '_'))
      {
        stepRight();
        return ParseResult.TRUE;
      }

      //Additional characters can also be...
      if (!bFirst)
      {
        if ((cCurrent >= '0') && (cCurrent <= '9'))
        {
          stepRight();
          return ParseResult.TRUE;
        }
      }
      return ParseResult.FALSE;
    }
    else
    {
      return ParseResult.ERROR;
    }
  }

  public ParseResult getExactCharacterSequence(String szSequence)
  {
    char cCurrent;
    int iPos;

    iPos = 0;
    pushPosition();
    skipWhiteSpace();

    //Make sure we're not off the end of the file.
    if (outsideText)
    {
      popPosition();
      return ParseResult.ERROR;
    }

    for (; ;)
    {
      if (iPos == szSequence.length())
      {
        //Got all the way to the NULL character.
        passPosition();
        return ParseResult.TRUE;
      }
      if (!outsideText)
      {
        cCurrent = text.get(position);
        if (cCurrent == szSequence.charAt(iPos))
        {
          stepRight();
          iPos++;
        }
        else
        {
          //Put the parser back where it was.
          popPosition();
          return ParseResult.FALSE;
        }
      }
      else
      {
        //Put the parser back where it was.
        popPosition();
        return ParseResult.FALSE;
      }
    }
  }

  public ParseResult getExactIdentifier(String szIdentifier)
  {
    CharPointer cCurrent = new CharPointer();
    int iPos;
    ParseResult tResult;

    iPos = 0;
    pushPosition();
    skipWhiteSpace();

    //Make sure we're not off the end of the file.
    if (outsideText)
    {
      popPosition();
      return ParseResult.ERROR;
    }

    for (; ;)
    {
      if (!outsideText)
      {
        cCurrent.value = text.get(position);
        if (iPos == szIdentifier.length())
        {
          //Got all the way to the NULL character.
          //If there are additional identifier characters then we do not have the right identifier.
          tResult = getIdentifierCharacter(cCurrent, iPos == 0);
          if (tResult == ParseResult.TRUE)
          {
            //Put the parser back where it was.
            popPosition();
            return ParseResult.FALSE;
          }
          passPosition();
          return ParseResult.TRUE;
        }
        if (cCurrent.value == szIdentifier.charAt(iPos))
        {
          stepRight();
          iPos++;
        }
        else
        {
          //Put the parser back where it was.
          popPosition();
          return ParseResult.FALSE;
        }
      }
      else
      {
        //Put the parser back where it was.
        popPosition();
        return ParseResult.FALSE;
      }
    }
  }

  public ParseResult getIdentifier(StringZero szIdentifier)
  {
    CharPointer c = new CharPointer();
    boolean bFirst;
    int iPos;

    bFirst = true;
    iPos = 0;
    pushPosition();
    skipWhiteSpace();

    //Make sure we're not off the end of the file.
    if (outsideText)
    {
      popPosition();
      return ParseResult.ERROR;
    }

    for (; ;)
    {
      if (!outsideText)
      {
        if (getIdentifierCharacter(c, bFirst) != ParseResult.TRUE)
        {
          if (bFirst)
          {

            szIdentifier.setEnd(iPos);
            popPosition();
            return ParseResult.FALSE;
          }
          else
          {
            szIdentifier.setEnd(iPos);
            passPosition();
            return ParseResult.TRUE;
          }
        }
        else
        {
          szIdentifier.set(iPos, c.value);
        }
      }
      else
      {
        if (bFirst)
        {
          popPosition();
          return ParseResult.ERROR;
        }
        else
        {
          szIdentifier.setEnd(iPos);
          passPosition();
          return ParseResult.TRUE;
        }
      }
      bFirst = false;
      iPos++;
    }
  }

  public ParseResult getString(StringZero szString)
  {
    int iPos;
    char cCurrent;
    ParseResult tReturn;

    pushPosition();
    skipWhiteSpace();

    if (!outsideText)
    {
      if (getExactCharacter('\"', false) == ParseResult.TRUE)
      {
        iPos = 0;
        for (; ;)
        {
          if (!outsideText)
          {
            cCurrent = text.get(position);
            if (cCurrent == '\"')
            {
              szString.setEnd(iPos);
              stepRight();
              passPosition();
              return ParseResult.TRUE;
            }
            //We have an escape character...
            else if (cCurrent == '\\')
            {
              tReturn = GetEscapeCode(new StringZero(szString, iPos));
              iPos++;
              if ((tReturn == ParseResult.FALSE) || (tReturn == ParseResult.ERROR))
              {
                popPosition();
                return ParseResult.ERROR;
              }
            }
            else if (cCurrent == '\n')
            {
              //Just ignore new lines.
              stepRight();
            }
            else if (cCurrent == '\r')
            {
              //Just ignore carriage returns.
              stepRight();
            }
            else
            {
              szString.set(iPos, cCurrent);
              iPos++;
              stepRight();
            }
          }
        }
      }
      else
      {
        //No quote so not a string.
        popPosition();
        return ParseResult.FALSE;
      }
    }
    else
    {
      popPosition();
      return ParseResult.ERROR;
    }
  }

  public ParseResult GetEscapeCode(StringZero c)
  {
    char cCurrent;

    if (!outsideText)
    {
      if (text.get(position) == '\\')
      {
        stepRight();
        if (!outsideText)
        {
          cCurrent = text.get(position);
          if (cCurrent == 'n')
          {
            c.set(0, '\n');
          }
          else if (cCurrent == '\\')
          {
            c.set(0, '\\');
          }
          else if (cCurrent == '\"')
          {
            c.set(0, '\"');
          }
          else
          {
            return ParseResult.ERROR;
          }
          stepRight();
          return ParseResult.TRUE;
        }
        else
        {
          return ParseResult.ERROR;
        }
      }
      else
      {
        return ParseResult.ERROR;
      }
    }
    else
    {
      return ParseResult.ERROR;
    }
  }

  public ParseResult GetDigit(IntegerPointer pi)
  {
    char cCurrent;

    if (!outsideText)
    {
      cCurrent = text.get(position);
      if ((cCurrent >= '0') && (cCurrent <= '9'))
      {
        pi.value = (int) (cCurrent - '0');
        stepRight();
        return ParseResult.TRUE;
      }
      else
      {
        return ParseResult.FALSE;
      }
    }
    else
    {
      return ParseResult.ERROR;
    }
  }

  public ParseResult GetSign(IntegerPointer pi)
  {
    char cCurrent;

    if (!outsideText)
    {
      pi.value = 1;
      cCurrent = text.get(position);
      if (cCurrent == '-')
      {
        pi.value = -1;
        stepRight();
        return ParseResult.TRUE;
      }
      else if (cCurrent == '+')
      {
        stepRight();
        return ParseResult.TRUE;
      }
      else
      {
        return ParseResult.FALSE;
      }
    }
    else
    {
      return ParseResult.ERROR;
    }
  }

  public ParseResult getInteger(IntegerPointer pi, IntegerPointer iNumDigits)
  {
    ParseResult tResult;

    pushPosition();
    skipWhiteSpace();

    //Make sure we're not off the end of the file.
    if (outsideText)
    {
      popPosition();
      return ParseResult.ERROR;
    }

    tResult = GetDigits(pi, iNumDigits);
    if (tResult == ParseResult.TRUE)
    {
      //Make sure there are no decimals.
      if (text.get(position) == '.')
      {
        popPosition();
        return ParseResult.FALSE;
      }

      passPosition();
      return ParseResult.TRUE;
    }
    popPosition();
    return tResult;
  }

  public ParseResult GetDigits(IntegerPointer pi, IntegerPointer iNumDigits)
  {
    int iNum;
    IntegerPointer iSign = new IntegerPointer();
    IntegerPointer iTemp = new IntegerPointer();
    ParseResult tReturn;
    boolean bFirstDigit;
    int i;

    pushPosition();
    skipWhiteSpace();

    pi.value = 0;
    i = 0;
    if (!outsideText)
    {
      iNum = 0;

      GetSign(iSign);
      bFirstDigit = true;
      for (; ;)
      {
        if (!outsideText)
        {
          tReturn = GetDigit(iTemp);
          if (tReturn == ParseResult.TRUE)
          {
            i++;
            iNum *= 10;
            iNum += iTemp.value;
          }
          else if ((tReturn == ParseResult.FALSE) || (tReturn == ParseResult.ERROR))
          {
            if (bFirstDigit)
            {
              //might already have got a sign...  so reset the parser.
              popPosition();
              return ParseResult.FALSE;
            }
            iNum *= iSign.value;
            pi.value = iNum;
            if (iNumDigits != null)
            {
              iNumDigits.value = i;
            }
            passPosition();
            return ParseResult.TRUE;
          }
          bFirstDigit = false;
        }
        else
        {
          //Got only a sign then end of file.
          popPosition();
          return ParseResult.ERROR;
        }
      }
    }
    else
    {
      popPosition();
      return ParseResult.ERROR;
    }
  }

  public ParseResult GetFloat(FloatPointer pf)
  {
    IntegerPointer iLeft = new IntegerPointer();
    IntegerPointer iRight = new IntegerPointer();
    ParseResult tReturn;
    IntegerPointer iNumDecimals = new IntegerPointer();
    double fLeft;
    double fRight;
    double fTemp;
    boolean bLeft;

    pushPosition();
    skipWhiteSpace();

    pf.value = 0.0;
    if (!outsideText)
    {
      //Try and get the mantissa.
      tReturn = GetDigits(iLeft, null);
      bLeft = true;

      //Just return on errors an non-numbers.
      if (tReturn == ParseResult.FALSE)
      {
        //There may still be a decimal point...
        iLeft.value = 0;
        bLeft = false;
      }
      else if (tReturn == ParseResult.ERROR)
      {
        popPosition();
        return ParseResult.ERROR;
      }

      fLeft = iLeft.value;
      tReturn = getExactCharacter('.', false);
      if (tReturn == ParseResult.TRUE)
      {
        tReturn = GetDigits(iRight, iNumDecimals);
        if (tReturn == ParseResult.TRUE)
        {
          fRight = iRight.value;
          fTemp = Math.pow(10.0f, (-iNumDecimals.value));
          fRight *= fTemp;

          pf.value = fLeft + fRight;
          passPosition();
          return ParseResult.TRUE;
        }
        else
        {
          //A decimal point must be followed by a number.
          popPosition();
          return ParseResult.ERROR;
        }
      }
      else
      {
        //No decimal point...
        if (!bLeft)
        {
          //No digits and no point...
          popPosition();
          return ParseResult.FALSE;
        }
        else
        {
          pf.value = fLeft;
          passPosition();
          return ParseResult.TRUE;
        }
      }
    }
    else
    {
      popPosition();
      return ParseResult.ERROR;
    }
  }

  public TextParserPosition saveSettings()
  {
    TextParserPosition textPosition = new TextParserPosition();
    textPosition.position = position;
    textPosition.positions = new ArrayList<Integer>(mcPositions);
    return textPosition;
  }

  public void LoadSettings(TextParserPosition textPosition)
  {
    position = textPosition.position;
    mcPositions = new ArrayList<Integer>(textPosition.positions);
    testEnd();
  }

  public ParseResult FindExactIdentifier(String szIdentifier)
  {
    int szPosition;
    ParseResult result;

    pushPosition();
    skipWhiteSpace();

    for (; ;)
    {
      szPosition = position;
      result = getExactIdentifier(szIdentifier);
      if (result == ParseResult.ERROR)
      {
        //We've reached the end of the file without finding the identifier.
        popPosition();
        return ParseResult.FALSE;
      }
      else if (result == ParseResult.FALSE)
      {
        //Try the next actual character along.
        stepRight();
        skipWhiteSpace();
      }
      else if (result == ParseResult.TRUE)
      {
        position = szPosition;
        passPosition();
        return ParseResult.TRUE;
      }
    }
  }

  public ParseResult FindExactCharacterSequence(String szSequence)
  {
    int szPosition;
    ParseResult result;

    pushPosition();
    skipWhiteSpace();

    for (; ;)
    {
      szPosition = position;
      result = getExactCharacterSequence(szSequence);
      if (result == ParseResult.ERROR)
      {
        //We've reached the end of the file without finding the identifier.
        popPosition();
        return ParseResult.FALSE;
      }
      else if (result == ParseResult.FALSE)
      {
        //Try the next actual character along.
        stepRight();
        skipWhiteSpace();
      }
      else if (result == ParseResult.TRUE)
      {
        position = szPosition;
        passPosition();
        return ParseResult.TRUE;
      }
    }
  }

  public void Restart()
  {
    intiailise();
    pushPosition();
    testEnd();
  }

  public ParseResult FindStartOfLine()
  {
    char cCurrent;

    pushPosition();

    //If we're off the end of the file we can't return the beginning of the line.
    if (outsideText)
    {
      popPosition();
      return ParseResult.ERROR;
    }

    for (; ;)
    {
      stepLeft();

      //If we have no more text then the start of the line is the start of the text.
      if (outsideText)
      {
        position = 0;
        passPosition();
        return ParseResult.TRUE;
      }

      //If we get find an end of line character we've gone to far, go right again.
      cCurrent = text.get(position);
      if ((cCurrent == '\n') || (cCurrent == '\r'))
      {
        stepRight();
        passPosition();
        return ParseResult.TRUE;
      }
    }
  }

  public void pushPosition()
  {
    mcPositions.add(new Integer(position));
  }

  public void popPosition()
  {
    position = mcPositions.get(mcPositions.size() - 1).intValue();
    mcPositions.remove(mcPositions.size() - 1);
    testEnd();
  }

  public void passPosition()
  {
    mcPositions.remove(mcPositions.size() - 1);
  }

// ----------------------- Helper Functions ------------------------------

  public ParseResult GetHFExactIdentifierAndInteger(String szIdentifier, IntegerPointer piInt)
  {
    ParseResult tReturn;

    pushPosition();

    tReturn = getExactIdentifier(szIdentifier);
    if ((tReturn == ParseResult.ERROR) || (tReturn == ParseResult.FALSE))
    {
      popPosition();
      return tReturn;
    }
    tReturn = getInteger(piInt, null);
    if ((tReturn == ParseResult.ERROR) || (tReturn == ParseResult.FALSE))
    {
      popPosition();
      return tReturn;
    }

    passPosition();
    return ParseResult.TRUE;
  }

  public ParseResult GetHFExactIdentifierAndString(String szIdentifier, StringZero szString)
  {
    ParseResult tReturn;

    pushPosition();

    tReturn = getExactIdentifier(szIdentifier);
    if ((tReturn == ParseResult.ERROR) || (tReturn == ParseResult.FALSE))
    {
      popPosition();
      return tReturn;
    }
    tReturn = getString(szString);
    if ((tReturn == ParseResult.ERROR) || (tReturn == ParseResult.FALSE))
    {
      popPosition();
      return tReturn;
    }

    passPosition();
    return ParseResult.TRUE;
  }

  public ParseResult GetHFDotDelimeteredIdentifier(StringBuffer identifier)
  {
    ParseResult tReturn;
    StringZero stringZero = new StringZero();

    for (int i = 0; ; i++)
    {
      tReturn = getIdentifier(stringZero);
      if (tReturn == ParseResult.ERROR)
      {
        return ParseResult.ERROR;
      }
      if (tReturn == ParseResult.FALSE)
      {
        if (i == 0)
        {
          return ParseResult.FALSE;
        }
        return ParseResult.TRUE;
      }
      identifier.append(stringZero.toString());

      tReturn = getExactCharacter('.', false);
      if (tReturn == ParseResult.ERROR)
      {
        return ParseResult.ERROR;
      }
      if (tReturn == ParseResult.FALSE)
      {
        return ParseResult.TRUE;
      }
      identifier.append('.');
    }
  }

  public ParseResult GetHFCharacterSequenceBetweenTags(StringZero destination, String startTag, String endTag)
  {
    ParseResult result;

    result = FindExactCharacterSequence(startTag);
    if (result.equals(ParseResult.TRUE))
    {
      TextParserPosition start = saveSettings();
      result = FindExactCharacterSequence(endTag);
      if (result.equals(ParseResult.TRUE))
      {
        TextParserPosition end = saveSettings();

        destination.copy(text, start.position, end.position + endTag.length());
        return ParseResult.TRUE;
      }
    }
    return result;
  }

  public String getRemaining()
  {
    return text.toStringFrom(position);
  }
}
