# généré avec chat GPT

# Variables
MAVEN_VERSION=3.9.6
MAVEN_DIR=apache-maven-$(MAVEN_VERSION)
MAVEN_ARCHIVE=$(MAVEN_DIR)-bin.tar.gz
MAVEN_URL=https://downloads.apache.org/maven/maven-3/$(MAVEN_VERSION)/binaries/$(MAVEN_ARCHIVE)
INSTALL_DIR=$(CURDIR)/tools
MAVEN_HOME=$(INSTALL_DIR)/$(MAVEN_DIR)
MVN=$(MAVEN_HOME)/bin/mvn

# Default target
all: install-maven run

# Download and install Maven if not present
install-maven:
	@echo "Checking Maven installation..."
	@if [ ! -d "$(MAVEN_HOME)" ]; then \
		echo "Downloading Maven $(MAVEN_VERSION)..."; \
		mkdir -p $(INSTALL_DIR); \
		cd $(INSTALL_DIR) && curl -O $(MAVEN_URL); \
		cd $(INSTALL_DIR) && tar -xzf $(MAVEN_ARCHIVE); \
		rm -f $(INSTALL_DIR)/$(MAVEN_ARCHIVE); \
	else \
		echo "Maven already installed."; \
	fi

# Compile the project using Maven
build:
	$(MVN) clean install

# Run the application
run: build
	$(MVN) clean javafx:run -pl graph-ui

# Clean target
clean:
	$(MVN) clean