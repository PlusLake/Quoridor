package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Library
{
	public static Color genColor(int c)
	{
		return new Color(c, c, c);
	}

	public static int getTextWidth(Font f,String s)
	{
		Graphics2D g = ((Graphics2D)(new BufferedImage(1,1,BufferedImage.TYPE_BYTE_BINARY).getGraphics()));
		g = getTextAntiAlias(g);
		g.setFont(f);
		Rectangle2D r = g.getFontMetrics().getStringBounds(s,g);

		return (int) r.getWidth();
	}

	public static Graphics2D getTextAntiAlias(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,	RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,	RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		return g2;
	}
}