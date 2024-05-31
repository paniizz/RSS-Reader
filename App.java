package demo;


import java.util.Scanner;
import org.jsoup.Jsoup;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;


public class App {

    public static Scanner scanner = new Scanner(System.in); 
    public static void retrieveRssContent(String rssUrl) {
        try {
            Document doc = Jsoup.connect(rssUrl).get();
            Elements itemElements = doc.select("item");

            for (int i = 0; i < 5 && i < itemElements.size(); ++i) {
                Element itemElement = itemElements.get(i);
                System.out.println("title: " + getElementTextContent(itemElement, "title"));
                System.out.println("link: " + getElementTextContent(itemElement, "link"));
                System.out.println("description: " + getElementTextContent(itemElement, "description"));
            }
        } catch (IOException e) {
            System.out.println("faild");
        }
    }

    
    public static String searchRSS(String site) {
        try (BufferedReader bReader = new BufferedReader(new FileReader("data.txt"))) {
            for (String l = bReader.readLine(); l != null; l = bReader.readLine()) {
                String[] p = l.split(";");
                if (p.length >= 3) {
                    String n = p[0];
                    String r = p[2];
                    if (n.equals(site)) {
                        return r;
                    }
                }
            }
            System.out.println("RSS for " + site + " not found in data");
        } catch (IOException e) {
            System.out.println("faild");
        }
        return "";
    }

    
    public static void readRssFromData() {
        try (BufferedReader bReader = new BufferedReader(new FileReader("data.txt"))) {
            java.util.List<String> l = new java.util.ArrayList<>();
            String line;
            while ((line = bReader.readLine()) != null) {
                l.add(line);
            }
            
            for (int i = 0; i < l.size(); i++) {
                String[] p = l.get(i).split(";");
                if (p.length >= 3) {
                    String rss = p[2];
                    retrieveRssContent(rss);
                }
            }
        } catch (IOException e) {
            System.out.println("faild");
        }
    }

    public static void addWebsiteURL() {
        System.out.println("Please enter website URL to add:");
        String inputURL = scanner.nextLine();
        
        if (pidakardanaddress(inputURL)) {
            System.out.println(inputURL + " already exists.");
            return;
        }

        String[] s = extractSiteInfo(inputURL);
        String n = s[0];
        String r = s[1];

        String output = n + ";" + inputURL;

        if (!r.isEmpty()) {
            output += ";" + r;
        } else {
            System.out.println("RSS URL not found!");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt", true))) {
            writer.write(output);
            writer.newLine();
            System.out.println("URL " + inputURL + " added successfully!");
        } catch (IOException e) {
            System.out.println("faild");
            e.printStackTrace();
        }
    }
    



    public static boolean pidakardanaddress(String url) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).contains(url)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("faild");
            e.printStackTrace();
        }

        return false;
    }

    public static String getElementTextContent(Element parentElement, String tagName) {
        Element element = parentElement.selectFirst(tagName);
        if (element != null) {
            return element.text();
        }
        return "";
    }
    
    public static String[] extractSiteInfo(String websiteUrl) {
        String[] siteInfo = new String[3];

        try {
            
            Document doc = Jsoup.connect(websiteUrl).get();

            String title = doc.title();
            siteInfo[0] = title;

            String rssUrl = doc.select("link[type='application/rss+xml']").attr("abs:href");
            siteInfo[1] = rssUrl;

            
        } catch (MalformedURLException e) {
            System.out.println("Invalid URL format!");
            return new String[]{"", ""};
        } catch (IOException e) {
            System.out.println("faild");
            return new String[]{"", ""};
        }

        return siteInfo;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int c;
        System.out.println("**Welcome to RSS Reader!**");

        do {
            System.out.println("Type a valid number for your desired action:");
            System.out.println("[1] Show updates");
            System.out.println("[2] Add URL");
            System.out.println("[3] Remove URL");
            System.out.println("[4] Exit");
            c = Integer.parseInt(scanner.nextLine());

            if (c == 3) {
                hazfeurl();
            } else if (c == 4) {
                System.out.println("Exiting...");
            } else if (c == 2) {
                addWebsiteURL();
            } else if (c == 1) {
                nmaieshurl();
            } else {
                System.out.println("faild");
            }

        } while (c != 4);

        scanner.close();
    }

 
    public static ArrayList<String> khondanurl(String filename) {
        ArrayList<String> titles = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String l;
            while ((l = reader.readLine()) != null) {
                lines.add(l);
            }
            
            for (int i = 0; i < lines.size(); i++) {
                String[] p = lines.get(i).split(";");
                if (p.length >= 1) {
                    titles.add(p[0]);
                }
            }
        } catch (IOException e) {
            System.out.println("faild");
            e.printStackTrace();
        }
        
        return titles;
    }

    public static void nmaieshurl() {
        ArrayList<String> t = khondanurl("data.txt");
        if (t.isEmpty()) {
            System.out.println("No RSS found!");
            return;
        }
    
        System.out.println("Show updates for:");
        System.out.println("[0] All website");
        for (int i = 0; i < t.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + t.get(i));
        }
        System.out.println("Enter -1 to return.");
    
        Scanner localScanner = new Scanner(System.in); 
        int choice = Integer.parseInt(localScanner.nextLine()); 
    
        if (choice == -1) {
            System.out.println("");
        } else if (choice == 0) {
            readRssFromData();
        } else {
            if (choice >= 1 && choice < t.size() + 1) {
                retrieveRssContent(searchRSS(t.get(choice - 1)));
            } else {
                System.out.println("Invalid choice!");
            }
        }
    
    }


    
    public static void hazfrurl(String siteName) {
        File inputFile = new File("data.txt");
        File fakefile = new File("tempData.txt");
    
        BufferedReader reader = null;
        BufferedWriter writer = null;
    
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            writer = new BufferedWriter(new FileWriter(fakefile));
    
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
    
            boolean found = false;
            for (int i = 0; i < lines.size(); i++) {
                String currentLine = lines.get(i);
                String[] parts = currentLine.split(";");
                if (parts.length >= 3) {
                    String name = parts[1].trim();
                    if (name.equals(siteName)) {
                        found = true;
                        continue; 
                    }
                    writer.write(currentLine + System.lineSeparator());
                }
            }
    
            if (!found) {
                fakefile.delete();
                System.out.println("Couldn't find " + siteName + ".");
            } else {
                System.out.println("Removed " + siteName + " successfully.");
            }
    
        } catch (IOException e) {
            
        } finally {
            
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    
                }
            }
        }
    
        
        inputFile.delete();
        fakefile.renameTo(inputFile);
    }

    public static void hazfeurl() {
        System.out.println("please enter website URL to remove:");
        String urlToRemove = scanner.nextLine();
        hazfrurl(urlToRemove);
    }

    
 
}
