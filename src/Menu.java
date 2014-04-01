import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class Menu {

	Database connection;
	Scanner sc;
	Statement stmt;
	ResultSet res;
	Connection conn;
	ResultSetMetaData rsmd;

	ArrayList<Ingrediens> inKitchen;
	ArrayList<Ingrediens> inRecipe;
	ArrayList<Ingrediens> shoppingList;

	public Menu(Database connection) throws SQLException {
		this.connection = connection;
		this.sc = new Scanner(System.in);
		stmt = connection.getStatement();
		conn = connection.getConnection();
		shoppingList = new ArrayList<Ingrediens>();
		inKitchen = new ArrayList<Ingrediens>();
		inRecipe = new ArrayList<Ingrediens>();
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

	/*
	 * Loopar menyn
	 */
	private void loopMenu() throws SQLException {
		System.out.println("");
		System.out.println("****** ****** \n");
		System.out.println("");
		printMenu();
	}

	/*
	 * Utför olika åtgärder beroende på vilket val användaren gör
	 */
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
				shoppingList.clear();
				inKitchen.clear();
				inRecipe.clear();
				if (possibleRecipe(recipeNameUpd)) {
					cookRecipe(recipeNameUpd);
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

	/*
	 * Case 1: Listar alla befintliga ingredienser i köket Metoden skriver ut
	 * ingrediensens namn och amount till konsoll
	 */
	private boolean listAllIngKitchen() {
		String query = "SELECT * FROM inkitchen";
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
	 * Case 3: Lägg till ingrediens till kök Kollar om ingrediensen redan
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
	 * Kollar om en ingrediens existerar i köket baserat på inkommande
	 * ingrediensnamn
	 */
	private boolean checkIfIngExists(String ingName) throws SQLException {
		String query = "SELECT * FROM inkitchen WHERE name = '" + ingName + "'";
		res = stmt.executeQuery(query);
		if (res.isBeforeFirst()) { // rader finns i resultSet
			return true;
		}
		return false;
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
	 * Case 5: kollar om tillräckligt med ingredienser finns i köket
	 */
	private boolean possibleRecipe(String recipeNameUpd) throws SQLException {
		if (ifIngExists(recipeNameUpd)) {
			return true;
		}
		return false;
	}

	/*
	 * check if all recipe ingredients exists in kitchen
	 */
	private boolean ifIngExists(String recipeName) throws SQLException {
		String query = "SELECT ingrediensname, amount FROM inrecipe WHERE recipename = '" + recipeName + "'";
		res = stmt.executeQuery(query); // alla ingredienser i ett recept
		addAll(res, inRecipe);

		// if (res.wasNull())
		// return true;
		// res.close();

		query = "SELECT name, amount FROM inkitchen";
		res = stmt.executeQuery(query); // alla ingredienser i köket
		addAll(res, inKitchen);

		// loopar genom alla ingredienser som receptet behöver samt kontrollerar
		// mängden
		Iterator<Ingrediens> it = inRecipe.iterator();
		while (it.hasNext()) {
			Ingrediens tempIng = it.next();
			if (!checkIngByName(tempIng, inKitchen) || !checkIngByAmount(tempIng, inKitchen))
				return false;
		}
		return true;
	}

	/*
	 * Skapar en ny ingredienslista från inkommande resultSet
	 */
	private void addAll(ResultSet resSet, ArrayList<Ingrediens> list) throws SQLException {
		while (resSet.next()) {
			String ingName = resSet.getString(1);
			int ingAmount = resSet.getInt(2);
			list.add(new Ingrediens(ingName, ingAmount));
		}
	}

	/*
	 * Slår upp ingrediensnamnet i köket
	 */
	private boolean checkIngByName(Ingrediens recIng, ArrayList<Ingrediens> kitchenList) {

		String recIngNamn = recIng.getName();
		int kitchenListSize = kitchenList.size();
		for (int n = 0; n < kitchenListSize; n++) {
			if (recIngNamn.equals(kitchenList.get(n).getName())) { // vi har en
				// match
				return true;
			}
		}
		return false;
	}

	/*
	 * Slår upp amount i köket
	 */
	private boolean checkIngByAmount(Ingrediens recIng, ArrayList<Ingrediens> kitchenList) {
		int recIngAmount = recIng.getAmount();
		String recIngName = recIng.getName();
		int kitchenListSize = kitchenList.size();

		for (int n = 0; n < kitchenListSize; n++) {
			int amountDiff = (kitchenList.get(n).getAmount() - recIngAmount);
			if (amountDiff >= 0) { // vi har tillräckligt
				shoppingList.add(new Ingrediens(recIngName, amountDiff));
				return true;
			} else {
				shoppingList.add(new Ingrediens(recIngName, amountDiff));
			}
		}
		return false;
	}

	/*
	 * Case 5: "Lagar" ett recept om tillräckligt med ingredienser finns
	 */
	private boolean cookRecipe(String recipeNameUpd) throws SQLException {
		int size = shoppingList.size();
		String query = ("SELECT * FROM inkitchen");
		res = stmt.executeQuery(query);

		for (int i = 0; i < size; i++) {
			Ingrediens tempIng = shoppingList.get(i);
			while (res.next()) {
				if (res.getString(1).equals(tempIng.getName())) {
					String queryUpd = "UPDATE inkitchen SET amount = '" + tempIng.getAmount() + "' WHERE name = '" + tempIng.getName() + "'";
					exQueryUpd(queryUpd);
					break;
				}
				res.beforeFirst();
			}
		}
		return true;
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
	 * Tar in ett ResultSet och skriver ut varje resultat till konsoll
	 */
	private void printResult(ResultSet res) throws SQLException {
		rsmd = res.getMetaData();
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
}
