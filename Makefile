compile:
	javac -encoding UTF-8 src/emulator/*.java -d out

run: compile
	java -cp out emulator.Main test_vfs

clean:
	rm -rf out