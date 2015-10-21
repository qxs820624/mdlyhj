/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jw.http;

import java.io.*;
/**
 *
 * @author simon
 */

public class CmwapInputStream extends InputStream
{
    //Added by Simon to support trunked
    //
    int nEncoding = 0;
    InputStream in;
	public CmwapInputStream(InputStream im, int encoding)
	{
		in = im;
        nEncoding = encoding;
        bReadSize = true;
	}

    public int read(byte[] b,
                int off,
                int len) throws IOException
    {
        for( int i = 0; i<len; ++i )
        {
            int data = read();
            if( data == -1 )
                return i;
            b[off+i] = (byte)data;
        }

        return len;
    }

    boolean bReadSize = false;
    int nCount;
    int nPos;
    
    public int read() throws IOException
	{
        if( nEncoding != 1 )
        {
            return in.read();
        }
        else
        {
            if( bReadSize )
            {
                String str = "";
                char ch;
                do
                {
                    do
                    {

                        int data = 0;

                        data = in.read();

                        if( data == -1 )
                            return -1;

                        ch = (char)data;
                        str += ch;
                    }while(ch != '\n');

                    str = str.trim();
                } while(str.length()==0);

                if( str.compareTo("0") == 0 )
                {// 0 chunk met: last one and read foot data
                    do
                    {
                      int data = in.read();
                      if ( data == -1 )
                          return -1;
                    }while( true );
                }
                else
                {
                    nCount = Integer.parseInt(str, 16);
                    nPos = 1;
                    if( nCount != 1 )
                        bReadSize = false;
                    return in.read();
                }

            }
            else
            {
                int data = in.read();
                nPos ++ ;
                if( nPos == nCount )
                {
                    bReadSize = true;
                    read();//Skip /r
                    read();//Skip /n
                }

                return data;
            }
        }
	}
}
