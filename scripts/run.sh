cd ../src/main/java/com
javac -cp jars/commons-io-2.8.0.jar models/*.java interfaces/*.java logging/*.java server/*.java utility/*.java threads/*.java test/*.java PeerToPeerFileSharing.java
cd ../
java -cp com/jars/commons-io-2.8.0.jar:. com.PeerToPeerFileSharing