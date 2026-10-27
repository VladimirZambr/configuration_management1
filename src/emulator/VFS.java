package emulator;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class VFS {
    public static class Node {
        String name;
        boolean isDirectory;
        Map<String, Node> children = new HashMap<>();
        byte[] content;
        Node parent;

        public Node(String name, boolean isDirectory, Node parent) {
            this.name = name;
            this.isDirectory = isDirectory;
            this.parent = parent;
        }
    }

    private Node root;
    private Node currentDir;

    public VFS(String xmlPath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(xmlPath));
            doc.getDocumentElement().normalize();

            root = new Node("/", true, null);
            currentDir = root;

            Element rootElement = doc.getDocumentElement();

            Element xmlRootDir = null;
            NodeList nodeList = rootElement.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                org.w3c.dom.Node n = nodeList.item(i);
                if (n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element el = (Element) n;
                    if ("dir".equals(el.getTagName()) && "root".equals(el.getAttribute("name"))) {
                        xmlRootDir = el;
                        break;
                    }
                }
            }
            if (xmlRootDir != null) {
                parseElement(xmlRootDir, root);
            } else {
                parseElement(rootElement, root);
            }
        } catch (Exception e) {
            System.err.println("[WARNING] Unable to load VFS from XML (" + xmlPath + "): " + e.getMessage());
            root = new Node("/", true, null);
            currentDir = root;
        }
    }

   private void parseElement(Element element, Node parentNode) {
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            org.w3c.dom.Node n = nodeList.item(i);
            if (n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element el = (Element) n;
                String tagName = el.getTagName();
                String name = el.getAttribute("name");

                if (tagName.equals("dir")) {
                    Node dirNode = new Node(name, true, parentNode);
                    parentNode.children.put(name, dirNode);
                    parseElement(el, dirNode);
                } else if (tagName.equals("file")) {
                    Node fileNode = new Node(name, false, parentNode);
                    String textContent = el.getTextContent().trim();
                    
                    if (!textContent.isEmpty()) {
                        String encoding = el.getAttribute("encoding");
                        if ("base64".equalsIgnoreCase(encoding)) {
                            try {
                                fileNode.content = Base64.getDecoder().decode(textContent);
                            } catch (IllegalArgumentException ex) {
                                fileNode.content = textContent.getBytes(StandardCharsets.UTF_8);
                            }
                        } else {
                            fileNode.content = textContent.getBytes(StandardCharsets.UTF_8);
                        }
                    } else {
                        fileNode.content = new byte[0];
                    }
                    parentNode.children.put(name, fileNode);
                }
            }
        }
    }

    public boolean changeDirectory(String path) {
        if (path.equals("..")) {
            if (currentDir.parent != null) {
                currentDir = currentDir.parent;
                return true;
            }
            return false;
        }
        if (path.equals("/")) {
            currentDir = root;
            return true;
        }
        if (path.startsWith("/")) {
            String[] segments = path.split("/");
            Node target = root;

            for (String segment : segments) {
                if (segment.isEmpty()) {
                    continue;
                }
                if (target.children.containsKey(segment)) {
                    Node next = target.children.get(segment);
                    if (next.isDirectory) {
                        target = next;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            currentDir = target;
            return true;
        }
        if (currentDir.children.containsKey(path)) {
            Node target = currentDir.children.get(path);
            if (target.isDirectory) {
                currentDir = target;
                return true;
            }
        }
        return false;
    }

    public List<String> listDirectory() {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Node> entry : currentDir.children.entrySet()) {
            String suffix = entry.getValue().isDirectory ? "/" : "";
            list.add(entry.getKey() + suffix);
        }
        return list;
    }


    public List<String> find(String searchArg) {
        List<String> results = new ArrayList<>();
        if (searchArg == null || searchArg.isEmpty()) {
            return results;
        }
        recursiveFind(currentDir, "", searchArg, results);
        return results;
    }

    private void recursiveFind(Node currentNode, String currentPath, String searchArg, List<String> results) {
        if (currentNode == null) {
            return;
        }
        for (Map.Entry<String, Node> entry : currentNode.children.entrySet()) {
            String name = entry.getKey();
            Node node = entry.getValue();
            String fullPath = currentPath.equals("") ? "/" + name : currentPath + "/" + name;
            if (name.equals(searchArg) || name.contains(searchArg)) {
                if (!results.contains(fullPath)) {
                    results.add(fullPath);
                }
            }
            if (node.isDirectory) {
                recursiveFind(node, fullPath, searchArg, results);
            }
        }
    }
}