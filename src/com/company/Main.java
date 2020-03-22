package com.company;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        Path currentPath = Paths.get("");

        while (true) {
            System.out.print(pathToString(currentPath) + ">");
            String in = scanner.nextLine();
            String[] splitIn = in.split(" ", 5);
            switch (splitIn[0]) {
                case "cd":
                    try {
                        currentPath = getPath(currentPath, splitIn[1]);
                    } catch (ArrayIndexOutOfBoundsException s) {
                        System.out.println("The command requires a second parameter");
                    }
                    break;
                case "cd..":
                    currentPath = getParentPath(currentPath);
                    break;
                case "exit":
                    return;
                case "dir":
                    directory(currentPath);
                    break;
                case "mkdir":
                    newDirectory(currentPath, splitIn[1]);
                    break;
                case "rename":
                    renameDirectory(currentPath, splitIn[1], splitIn[2]);
                    break;
                case "del":
                    delFile(currentPath, splitIn[1]);
                    break;
                case "ver":
                    String system = System.getProperty("os.name", "unknown").toLowerCase(Locale.ROOT);
                    System.out.println(system);
                    break;
                case "cls":
                    clearConsole();
                    break;
                case "replace":
                    replace(currentPath, splitIn[1], splitIn[2], splitIn[3], splitIn[4]);
                    break;
                case "rmdir":
                    delEmptyDirectory(currentPath, splitIn[1]);
                    break;
                case "date":
                    Date nowDate = new Date();
                    SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
                    System.out.println("The current date is: " + sd.format(nowDate));
                    break;
                case "time":
                    Date time = new Date();
                    SimpleDateFormat sd1 = new SimpleDateFormat("HH:mm:ss,S");
                    System.out.println("The current time is: " + sd1.format(time));
                    break;
                case "attribe":
                    attribeFile(currentPath, splitIn[1]);
                    break;
                case "getmac":
                    getMac();
                    break;
                case "hostname":
                    hostName();
                    break;
                case "ipconfig":
                    getIp();
                    break;
                case "copy":
                    copyFile(currentPath, splitIn[1], splitIn[2]);
                    break;
                case "help":
                    help();
                    break;
                default:
                    System.out.println("Command not found");
            }
        }
    }

    public static String pathToString(Path path) {
        return path.toAbsolutePath().toString();
    }

    public static void attribeFile(Path currentPath, String nameFile) {
        Path tempPath = Paths.get(pathToString(currentPath) + "\\" + nameFile);
        System.out.println("            " + pathToString(tempPath));
    }

    public static Path getPath(Path currentPath, String directory) {
        Path tempPath = Paths.get(pathToString(currentPath) + "\\" + directory);
        if (Files.exists(tempPath))
            return tempPath;
        else
            System.out.println("Directory not found");
        return currentPath;
    }

    public static void newDirectory(Path currentPath, String newFile) throws IOException {
        Path tempPath = Paths.get(pathToString(currentPath) + "\\" + newFile);
        if (Files.exists(tempPath)) {
            System.out.println("Directory is exists");
        } else {
            Files.createDirectories(tempPath);
        }
    }

    public static void renameDirectory(Path currentPath, String nameFile, String newNameFile) throws IOException {
        Path tempPath = Paths.get(pathToString(currentPath) + "\\" + nameFile);
        Path tempPath1 = Paths.get(pathToString(currentPath) + "\\" + newNameFile);
        if (Files.exists(tempPath)) {
            Files.move(tempPath, tempPath1);
        } else {
            System.out.println("No file");
        }
    }

    public static void delFile(Path currentPath, String nameFile) throws IOException {
        Path tempPath = Paths.get(pathToString(currentPath) + "\\" + nameFile);
        if (Files.exists(tempPath)) {
            List<Path> pathList = Files.list(tempPath).collect(Collectors.toList());
            for (Path p : pathList) {
                Files.delete(p);
            }
            Files.delete(tempPath);
        } else {
            System.out.println("No file");
        }
    }

    public static Path getParentPath(Path currentPath) {
        Path tempPath = Paths.get(pathToString(currentPath));
        tempPath = tempPath.getParent();
        if (tempPath != null)
            return tempPath;
        else
            System.out.println("No directory");
        return currentPath;
    }


    public static void directory(Path currentPath) throws IOException {
        System.out.println("Directory: " + pathToString(currentPath));
        List<Path> pathList = Files.list(currentPath).collect(Collectors.toList());
        System.out.println();
        int fileCounter = 0;
        int dirCounter = 0;
        for (Path p : pathList) {
            try {
                BasicFileAttributes attr = Files.readAttributes(p, BasicFileAttributes.class);
                if (attr.isDirectory())
                    dirCounter++;
                else
                    fileCounter++;
                System.out.println(attr.creationTime() + "  " + (attr.isDirectory() ? "<DIR> " : "      ") + p.getFileName());
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        System.out.println("Number of files: " + fileCounter);
        System.out.println("Number of directories: " + dirCounter);
    }

    public static void clearConsole() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void delEmptyDirectory(Path currentPath, String files) throws IOException {
        Path tempPath = Paths.get(pathToString(currentPath) + "\\" + files);
        if (Files.exists(tempPath) && files.length() == 0) {
            Files.delete(tempPath);
        } else if (files.length() > 0) {
            System.out.println("File is not empty");
            System.out.println("Are you sure you want to delete the file? yes/no");
            Scanner sc = new Scanner(System.in);
            String in = sc.nextLine();
            switch (in) {
                case "yes":
                    List<Path> pathList = Files.list(tempPath).collect(Collectors.toList());
                    for (Path p : pathList) {
                        Files.delete(p);
                    }
                    Files.delete(tempPath);
                    break;
                case "no":
                    break;
                default:
                    System.out.println("zle wprowadzone dane");
            }
        } else {
            System.out.println("Brak pliku");
        }
    }

    public static void getMac() throws SocketException, UnknownHostException {
        InetAddress ip;
        ip = InetAddress.getLocalHost();
        NetworkInterface network = NetworkInterface.getByInetAddress(ip);
        byte[] mac = network.getHardwareAddress();
        System.out.print("Your current MAC address is: ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        System.out.println(sb.toString());
    }

    public static void hostName() throws UnknownHostException {
        InetAddress ip;
        String hostName;
        try {
            ip = InetAddress.getLocalHost();
            hostName = ip.getHostName();
            System.out.println(hostName);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void getIp() throws UnknownHostException {
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            System.out.println("Your current IP address is: " + ip.getHostAddress());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void copyFile(Path currentPath, String copyFile, String newFile) throws IOException {
        Path tempPath = Paths.get(pathToString(currentPath) + "\\" + copyFile);
        Path tempPath1 = Paths.get(pathToString(currentPath) + "\\" + newFile);
        if (Files.exists(tempPath)) {
            try {
                Files.copy(tempPath, Files.createDirectories(tempPath1));
            } catch (Exception e) {
                System.out.println(e);
            }
        } else
            System.out.println("File not found");
    }

    public static void replace(Path currentPath, String drive1, String nameFile1, String drive2, String nameFile2) throws IOException {
        String tempName = "temp";
        Path driverPath1 = Paths.get(pathToString(currentPath) + File.separator + drive1);
        Path driverPath2 = Paths.get(pathToString(currentPath) + File.separator + drive2);
        Path tempFile1 = Paths.get(pathToString(driverPath1) + File.separator + nameFile1 + tempName);
        Path tempFile2 = Paths.get(pathToString(driverPath2) + File.separator + nameFile2 + tempName);
        Path file1 = Paths.get(pathToString(driverPath1) + File.separator + nameFile1);
        Path file2 = Paths.get(pathToString(driverPath2) + File.separator + nameFile2);
        if (Files.exists(file1) && Files.exists(file2)) {
            try {
                Files.move(file1, tempFile2);
                Files.move(file2, tempFile1);
                Files.move(tempFile1, file1);
                Files.move(tempFile2, file2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            System.out.println("File not found");
    }

    public static void help() throws FileNotFoundException {
        Scanner sc = new Scanner(new File("C:\\Users\\pekal\\IdeaProjects\\consoleCMD\\src\\help"));
        while (sc.hasNextLine()) {
            String txt = sc.nextLine();
            System.out.println(txt);
        }
        sc.close();
    }

}
