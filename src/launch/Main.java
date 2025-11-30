package launch;

import java.io.*;
import utils.ConsoleMenu;
import utils.VehicleConsoleCrudManager;

public class Main {

	public static void main(String[] args) throws IOException {

		String dataFile = "dataFile.txt";
		String logFile = "log.log";
		
		ConsoleMenu menu = new ConsoleMenu();
		VehicleConsoleCrudManager manager = new VehicleConsoleCrudManager(dataFile, logFile);

		menu.addItem("1", "Добавить транспорт", () -> manager.addVehicle());
		menu.addItem("2", "Обновить транспорт", () -> manager.updateVehicle());
		menu.addItem("3", "Удалить транспорт", () -> manager.deleteVehicle());
		menu.addItem("4", "Показать все", () -> manager.listVehicles());
		menu.addItem("0", "Выход", () -> {
		    manager.persist();
		});

		menu.run();
	}

}
