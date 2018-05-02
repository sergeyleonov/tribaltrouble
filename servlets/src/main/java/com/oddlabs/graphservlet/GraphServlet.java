package com.oddlabs.graphservlet;

import javax.imageio.ImageIO;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public final class GraphServlet extends HttpServlet {

	private static final int IMAGE_WIDTH = 532;
	private static final int IMAGE_HEIGHT = 200;
	private static final int BACKGROUND_COLOR = 0xFFFFFF;

	public final void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("image/png");
		String game_id_string = req.getParameter("game_id");
		int game_id;
		try {
			game_id = Integer.parseInt(game_id_string);
		} catch (NumberFormatException e) {
			res.sendError(500, e.getMessage());
			return;
		}
		try {
			Connection conn = getConnection();
			try {
				int[][] data = getGameData(conn, game_id);
				printResult(res.getOutputStream(), data);
			} finally {
				conn.close();
			}
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}

	private static Connection getConnection() {
		try {
			DataSource dataSource = InitialContext.doLookup("jdbc/graphDB");
			return dataSource.getConnection();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private int[][] getGameData(Connection conn, int game_id) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT tick, team1, team2, team3, team4, team5, team6 FROM game_reports WHERE game_id = ?");
		stmt.setInt(1, game_id);
		ResultSet result = stmt.executeQuery();
		ArrayList list = new ArrayList();
		while (result.next()) {
			list.add(new int[]{result.getInt("tick"), result.getInt("team1"), result.getInt("team2"), result.getInt("team3"), result.getInt("team4"), result.getInt("team5"), result.getInt("team6")});
		}
		int[][] array = new int[list.size()][];
		for (int i = 0; i < array.length; i++) {
			array[i] = (int[]) list.get(i);
		}
		return array;
	}

	private void printResult(OutputStream out, int[][] data) {
		BufferedImage img = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setColor(new Color(BACKGROUND_COLOR));
		g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
		g.setColor(Color.BLACK);
		g.drawLine(0, IMAGE_HEIGHT - 1, IMAGE_WIDTH - 1, IMAGE_HEIGHT - 1);
		g.drawLine(0, 0, 0, IMAGE_HEIGHT - 1);

		if (data.length > 0) {
			int max_x = 0;
			int max_y = 0;
			for (int[] aData : data) {
				if (aData[0] > max_x)
					max_x = aData[0];
				for (int j = 1; j < aData.length; j++) {
					if (aData[j] > max_y)
						max_y = aData[j];
				}
			}
			for (int i = 0; i < data.length; i++) {
				int a = i + 1;
				int x = data[i][0] * IMAGE_WIDTH / max_x;
				int y = IMAGE_HEIGHT - 1;
				if ((a % 30) == 0) {
					g.drawLine(x, y, x, y - 10);
				} else if ((a % 15) == 0) {
					g.drawLine(x, y, x, y - 7);
				} else if ((a % 3) == 0) {
					g.drawLine(x, y, x, y - 3);
				}
			}
			Color[] team_colors = new Color[]{
					new Color(1f, .75f, 0f),
					new Color(0f, .5f, 1f),
					new Color(1f, 0f, .25f),
					new Color(0f, 1f, .75f),
					new Color(.75f, 0f, 1f),
					new Color(.75f, 1f, 0f)};

			for (int j = 1; j < data[0].length; j++) {
				g.setColor(team_colors[j - 1]);
				int last_x = 0;
				int last_y = IMAGE_HEIGHT;
				for (int[] aData : data) {
					int x = aData[0] * IMAGE_WIDTH / max_x;
					int y = IMAGE_HEIGHT - aData[j] * IMAGE_HEIGHT / max_y;
					g.drawLine(last_x, last_y, x, y);
					last_x = x;
					last_y = y;
				}
			}
		}
		try {
			ImageIO.write(img, "png", out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
