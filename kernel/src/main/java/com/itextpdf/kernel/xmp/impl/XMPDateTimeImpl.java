//Copyright (c) 2006, Adobe Systems Incorporated
//All rights reserved.
//
//        Redistribution and use in source and binary forms, with or without
//        modification, are permitted provided that the following conditions are met:
//        1. Redistributions of source code must retain the above copyright
//        notice, this list of conditions and the following disclaimer.
//        2. Redistributions in binary form must reproduce the above copyright
//        notice, this list of conditions and the following disclaimer in the
//        documentation and/or other materials provided with the distribution.
//        3. All advertising materials mentioning features or use of this software
//        must display the following acknowledgement:
//        This product includes software developed by the Adobe Systems Incorporated.
//        4. Neither the name of the Adobe Systems Incorporated nor the
//        names of its contributors may be used to endorse or promote products
//        derived from this software without specific prior written permission.
//
//        THIS SOFTWARE IS PROVIDED BY ADOBE SYSTEMS INCORPORATED ''AS IS'' AND ANY
//        EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//        WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//        DISCLAIMED. IN NO EVENT SHALL ADOBE SYSTEMS INCORPORATED BE LIABLE FOR ANY
//        DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
//        (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//        LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
//        ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//        (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//        SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
//        http://www.adobe.com/devnet/xmp/library/eula-xmp-library-java.html

package com.itextpdf.kernel.xmp.impl;

import com.itextpdf.kernel.xmp.XMPDateTime;
import com.itextpdf.kernel.xmp.XMPException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


/**
 * The implementation of <code>XMPDateTime</code>. Internally a <code>calendar</code> is used
 * plus an additional nano seconds field, because <code>Calendar</code> supports only milli
 * seconds. The <code>nanoSeconds</code> convers only the resolution beyond a milli second.
 * 
 * @since 16.02.2006
 */
public class XMPDateTimeImpl implements XMPDateTime 
{
	/** */
	private int year = 0;
	/** */
	private int month = 0;
	/** */
	private int day = 0;
	/** */
	private int hour = 0;
	/** */
	private int minute = 0;
	/** */
	private int second = 0;
	/** Use NO time zone as default */
	private TimeZone timeZone = null;
	/**
	 * The nano seconds take micro and nano seconds, while the milli seconds are in the calendar.
	 */
	private int nanoSeconds;
	/** */
	private boolean hasDate = false;
	/** */
	private boolean hasTime = false;
	/** */
	private boolean hasTimeZone = false;
	

	/**
	 * Creates an <code>XMPDateTime</code>-instance with the current time in the default time
	 * zone.
	 */
	public XMPDateTimeImpl()
	{
		// EMPTY
	}

	
	/**
	 * Creates an <code>XMPDateTime</code>-instance from a calendar.
	 * 
	 * @param calendar a <code>Calendar</code>
	 */
	public XMPDateTimeImpl(Calendar calendar)
	{
		// extract the date and timezone from the calendar provided
        Date date = calendar.getTime();
        TimeZone zone = calendar.getTimeZone();

        // put that date into a calendar the pretty much represents ISO8601
        // I use US because it is close to the "locale" for the ISO8601 spec
        GregorianCalendar intCalendar = 
        	(GregorianCalendar) Calendar.getInstance(Locale.US);
        intCalendar.setGregorianChange(new Date(Long.MIN_VALUE));       
        intCalendar.setTimeZone(zone);           
        intCalendar.setTime(date); 		
		
		this.year = intCalendar.get(Calendar.YEAR);
		this.month = intCalendar.get(Calendar.MONTH) + 1; // cal is from 0..12
		this.day = intCalendar.get(Calendar.DAY_OF_MONTH);
		this.hour = intCalendar.get(Calendar.HOUR_OF_DAY);
		this.minute = intCalendar.get(Calendar.MINUTE);
		this.second = intCalendar.get(Calendar.SECOND);
		this.nanoSeconds = intCalendar.get(Calendar.MILLISECOND) * 1000000;
		this.timeZone = intCalendar.getTimeZone();

		// object contains all date components
		hasDate = hasTime = hasTimeZone = true;
	}

	
	/**
	 * Creates an <code>XMPDateTime</code>-instance from 
	 * a <code>Date</code> and a <code>TimeZone</code>.
	 * 
	 * @param date a date describing an absolute point in time
	 * @param timeZone a TimeZone how to interpret the date
	 */
	public XMPDateTimeImpl(Date date, TimeZone timeZone)
	{
		GregorianCalendar calendar = new GregorianCalendar(timeZone);
		calendar.setTime(date);
		
		this.year = calendar.get(Calendar.YEAR);
		this.month = calendar.get(Calendar.MONTH) + 1; // cal is from 0..12
		this.day = calendar.get(Calendar.DAY_OF_MONTH);
		this.hour = calendar.get(Calendar.HOUR_OF_DAY);
		this.minute = calendar.get(Calendar.MINUTE);
		this.second = calendar.get(Calendar.SECOND);
		this.nanoSeconds = calendar.get(Calendar.MILLISECOND) * 1000000;
		this.timeZone = timeZone;

		// object contains all date components
		hasDate = hasTime = hasTimeZone = true;
	}

	
	/**
	 * Creates an <code>XMPDateTime</code>-instance from an ISO 8601 string.
	 * 
	 * @param strValue an ISO 8601 string
	 * @throws XMPException If the string is a non-conform ISO 8601 string, an exception is thrown
	 */
	public XMPDateTimeImpl(String strValue) throws XMPException
	{
		ISO8601Converter.parse(strValue, this);
	}


	/**
	 * @see XMPDateTime#getYear()
	 */
	public int getYear()
	{
		return year;
	}

	
	/**
	 * @see XMPDateTime#setYear(int)
	 */
	public void setYear(int year)
	{
		this.year = Math.min(Math.abs(year), 9999);
		this.hasDate = true;
	}	
	

	/**
	 * @see XMPDateTime#getMonth()
	 */
	public int getMonth()
	{
		return month;
	}


	/**
	 * @see XMPDateTime#setMonth(int)
	 */
	public void setMonth(int month)
	{
		if (month < 1)
		{	
			this.month = 1;
		}
		else if (month > 12)
		{
			this.month = 12;
		}
		else
		{
			this.month = month;
		}

		this.hasDate = true;
	}

	
	/**
	 * @see XMPDateTime#getDay()
	 */
	public int getDay()
	{
		return day;
	}


	/**
	 * @see XMPDateTime#setDay(int)
	 */
	public void setDay(int day)
	{
		if (day < 1)
		{	
			this.day = 1;
		}
		else if (day > 31)
		{
			this.day = 31;
		}
		else
		{
			this.day = day;
		}
		
		this.hasDate = true;
	}
	
	
	/**
	 * @see XMPDateTime#getHour()
	 */
	public int getHour()
	{
		return hour;
	}

	
	/**
	 * @see XMPDateTime#setHour(int)
	 */
	public void setHour(int hour)
	{
		this.hour = Math.min(Math.abs(hour), 23);
		this.hasTime = true;
	}


	/**
	 * @see XMPDateTime#getMinute()
	 */
	public int getMinute()
	{
		return minute;
	}

	
	/**
	 * @see XMPDateTime#setMinute(int)
	 */
	public void setMinute(int minute)
	{
		this.minute = Math.min(Math.abs(minute), 59);
		this.hasTime = true;
	}

	
	/**
	 * @see XMPDateTime#getSecond()
	 */
	public int getSecond()
	{
		return second;
	}

	
	/**
	 * @see XMPDateTime#setSecond(int)
	 */
	public void setSecond(int second)
	{
		this.second = Math.min(Math.abs(second), 59);
		this.hasTime = true;
	}


	/**
	 * @see XMPDateTime#getNanoSecond()
	 */
	public int getNanoSecond()
	{
		return nanoSeconds;
	}

	
	/**
	 * @see XMPDateTime#setNanoSecond(int)
	 */
	public void setNanoSecond(int nanoSecond)
	{
		this.nanoSeconds = nanoSecond;
		this.hasTime = true;
	}
	

	/**
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(Object dt)
	{
		long d = getCalendar().getTimeInMillis()
				- ((XMPDateTime) dt).getCalendar().getTimeInMillis();
		if (d != 0)
		{
			return (int) Math.signum(d);
		}
		else
		{
			// if millis are equal, compare nanoseconds
			d = nanoSeconds - ((XMPDateTime) dt).getNanoSecond();
			return (int) Math.signum(d);
		}
	}


	/**
	 * @see XMPDateTime#getTimeZone()
	 */
	public TimeZone getTimeZone()
	{
		return timeZone;
	}


	/**
	 * @see XMPDateTime#setTimeZone(java.util.TimeZone)
	 */
	public void setTimeZone(TimeZone timeZone)
	{
		this.timeZone = timeZone;
		this.hasTime = true;
		this.hasTimeZone = true;
	}

	
	/**
	 * @see XMPDateTime#hasDate()
	 */
	public boolean hasDate()
	{
		return this.hasDate;
	}


	/**
	 * @see XMPDateTime#hasTime()
	 */
	public boolean hasTime()
	{
		return this.hasTime;
	}


	/**
	 * @see XMPDateTime#hasTimeZone()
	 */
	public boolean hasTimeZone()
	{
		return this.hasTimeZone;
	}
	

	/**
	 * @see XMPDateTime#getCalendar()
	 */
	public Calendar getCalendar()
	{
		GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance(Locale.US);
		calendar.setGregorianChange(new Date(Long.MIN_VALUE));
		if (hasTimeZone)
		{	
			calendar.setTimeZone(timeZone);
		}
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, nanoSeconds / 1000000);
	
		return calendar;
	}


	/**
	 * @see XMPDateTime#getISO8601String()
	 */
	public String getISO8601String()
	{
		return ISO8601Converter.render(this);
	}

	
	/**
	 * @return Returns the ISO string representation.
	 */
	public String toString()
	{
		return getISO8601String();
	}
}