import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Scanner;

public class Menu {

	Database connection;
	Scanner sc;
	Statement stmt;
	ResultSet res;
	Connection conn;

	public Menu(Database connection) throws SQLException {
		this.connection = connection;
		this.sc = new Scanner(System.in);
		stmt = connection.getStatement();
		conn = connection.getConnection();
		printWelcomeMessage();
	}

	/*
	 * Skriver ut väkomstmeddelande
	 */
	private void printWelcomeMessage() throws SQLException {
		System.out.println("Welcome to kitchenManager");
		printMenu();
	}

	/*
	 * Skriver ut alla meny-alternativ till konsoll
	 */
	private void printMenu() throws SQLException {
		System.out.println("*** MENU ***");
		System.out.println("1. List all ingrediens in kitchen");
		System.out.println("2. Show recipes");
		System.out.println("3. Been shopping");
		System.out.println("4. Get recipe ingrediense list");
		System.out.println("5. Cook recipe");
		System.out.println("6. Get possible recipes based on current food storage");
		System.out.println("7. Choose recipes för grocery shopping");
		System.out.println("8. Quit");

		int choice = getCommandInt();
		sc.nextLine();
		executeCommand(choice);
	}

	private void loopMenu() throws SQLException {
		System.out.println("");
		System.out.println("****** ****** \n");
		System.out.println("");
		printMenu();
	}

	private void executeCommand(int choice) throws SQLException {
		switch (choice) {
			case 1:
				choice = 1;
				System.out.println("***INGREDIENS IN KITCHEN*** \n");
				System.out.println("Ingrd. \t\t amount \n");
				listAllIngKitchen();
				loopMenu();
				break;
			case 2:
				choice = 2;
				System.out.println("***RECIPE*** \n");
				listAllRec();
				loopMenu();
				break;
			case 3:
				choice = 3;
				System.out.println("ADD INGREDIENT \n");
				System.out.println("Enter ingredient name: ");
				String ingredientName = getCommandString();
				System.out.println("Enter amount: ");
				int ingredientAmount = getCommandInt();
				boolean success = addToKitchen(ingredientName, ingredientAmount);

				if (success) {
					System.out.println("Ingrediens added \n");
				} else
					System.out.println("Ingrediens not added \n");
				loopMenu();
				break;

			case 4:
				choice = 4;
				System.out.println("Enter recipes name \n");
				String recipeName = getCommandString();
				System.out.println("Ingrd. \t\t amount \n");
				listAllIngRec(recipeName);
				loopMenu();
				break;

			case 5:
				choice = 5;
				System.out.println("Enter recipes name \n");
				String recipeNameUpd = getCommandString();
				if (possibleRecipe(recipeNameUpd)) {
					updateKitchen(recipeNameUpd);
					System.out.println("Kitchen storage updated! \n");
				} else
					System.out.println("Not enough ingredients");
				loopMenu();
				break;

			default:
				String invalid = "Invalid input";
				System.out.println(invalid);
				loopMenu();
				break;
		}
	}

	private boolean possibleRecipe(String recipeNameUpd) {
		String query = "SELECT * FROM inrecipe A JOIN inkitchen" + " B ON A.ingrediensname = B.name WHERE A.recipename = '" + recipeNameUpd + "'";
		return true;
	}

	/*
	 * Case 5:
	 */
	private boolean updateKitchen(String recipeNameUpd) {
		String query = "SELECT * FROM recipes";
		return exQuery(query);

	}

	/*
	 * Case 2: Listar alla ingredienser i ett recept
	 */
	private boolean listAllRec() {
		String query = "SELECT * FROM recipes";
		return exQuery(query);
	}

	/*
	 * Case 1: Listar alla befintliga ingredienser i köket Metoden skriver ut
	 * ingrediensens namn och amount till konsoll
	 */
	private boolean listAllIngKitchen() {
		String query = "SELECT * FROM inkitchen";
		return exQuery(query);
	}

	/*
	 * Case 4: Listar alla befintliga ingredienser i ett recept (från input)
	 * Metoden skriver ut ingrediensens namn och amount till konsoll
	 */
	private boolean listAllIngRec(String recipeName) {
		String query = "SELECT ingrediensname, amount FROM inrecipe WHERE recipename = '" + recipeName + "'";
		return exQuery(query);

	}

	/*
	 * Case 3 Lägg till ingrediens till kök Kollar om ingrediensen redan
	 * existerar och i sådana fall uppdaterar med rätt amount Annars lägger till
	 * ingrediensen
	 */
	private boolean addToKitchen(String ingredientName, int ingredientAmount) throws SQLException {
		if (checkIfIngExists(ingredientName)) {
			String query = "UPDATE inkitchen SET amount = amount + " + ingredientAmount + " WHERE name = '" + ingredientName + "'";
			return exQueryUpd(query);
		} else {
			String query = "INSERT INTO inkitchen (name, amount) VALUES ('" + ingredientName + "', '" + ingredientAmount + "')";
			return exQueryUpd(query);
		}
	}

	/*
	 * Returnerar int från input via konsoll
	 */
	private int getCommandInt() {
		int choice = sc.nextInt();
		return choice;
	}

	/*
	 * Returnerar textstäng från input via konsoll
	 */
	private String getCommandString() {
		String commandString = sc.nextLine();
		return commandString;
	}

	/*
	 * Exekverar en query och skapar ett resultatobjekt för utskrift Skriver ut
	 * resultatet på konsoll
	 */
	private boolean exQuery(String query) {
		try {
			res = stmt.executeQuery(query);
			printResult(res);
			return true;
		} catch (SQLException e) {
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}

	}

	/*
	 * Exekverar en query för uppdatering av en tabell-rad
	 */
	private boolean exQueryUpd(String query) {
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
			return true;
		} catch (SQLException e) {
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}

	}

	/*
	 * Tar in ett ResultSet och skriver ut varje resultat till konsoll
	 */
	private void printResult(ResultSet res) throws SQLException {
		ResultSetMetaData rsmd = res.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		while (res.next()) {
			for (int i = 1; i < columnsNumber + 1; i++) {
				if (columnTypeIsString(i, rsmd)) {
					System.out.print(res.getString(i) + "\t\t");
				} else
					System.out.print(res.getInt(i) + "\t\t");
			}
			System.out.println("");
		}
	}

	/*
	 * Kollar om innehållet i en column är en textsträng
	 */
	private boolean columnTypeIsString(int i, ResultSetMetaData rsmd) throws SQLException {
		int sqlType = rsmd.getColumnType(i);
		if (sqlType == Types.INTEGER) {
			return false;
		}
		return true;

	}

	/*
	 * Kollar om en ingrediens existerar i köket
	 */
	private boolean checkIfIngExists(String ingName) throws SQLException {
		String query = "SELECT * FROM inkitchen WHERE name = '" + ingName + "'";
		res = stmt.executeQuery(query);
		if (res.isBeforeFirst()) { // rader finns i resultSet
			return true;
		}
		return false;
	}
}
