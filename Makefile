compile:
	javac -encoding UTF-8 src/emulator/*.java -d out

run: compile
	java -cp out emulator.Main

clean:
	rm -rf out

test: compile
	@echo "[TEST MINIMAL]"
	bash tests/test1.sh
	@echo " "
	@echo "[TEST MEDIUM]"
	bash tests/test2.sh
	@echo " "
	@echo "[TEST BIG]"
	bash tests/test3.sh