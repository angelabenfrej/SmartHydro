#!/bin/bash

# Variables
REPO_PATH="/opt/SmartHydro"
BRANCH="master"
WILDFLY_DEPLOY="/opt/wildfly/standalone/deployments"
PWA_DEPLOY="/var/www/root"

# Navigate to the repository
cd "$REPO_PATH" || { echo "Repository path not found."; exit 1; }

# Fetch the latest changes from the remote repository
git fetch origin

# Compare the local branch with the remote branch
LOCAL_HASH=$(git rev-parse $BRANCH)
REMOTE_HASH=$(git rev-parse origin/$BRANCH)

if [ "$LOCAL_HASH" != "$REMOTE_HASH" ]; then
    echo "There are changes in the $BRANCH branch. Pulling the latest version and building projects..."

    # Pull the latest changes
    git pull origin $BRANCH

    # Build and deploy IAM
    echo "Building and deploying IAM..."
    cd iam || { echo "IAM directory not found."; exit 1; }
    mvn clean package verify
    sudo cp -r target/iam-1.0.war "$WILDFLY_DEPLOY/"
    cd ..

    # Build and deploy API
    echo "Building and deploying API..."
    cd api || { echo "API directory not found."; exit 1; }
    mvn clean package verify
    sudo cp -r target/api-1.0.war "$WILDFLY_DEPLOY/"
    cd ..

    # Deploy PWA
    echo "Deploying PWA..."
    sudo cp -r pwa/ "$PWA_DEPLOY/"

    echo "Build and deployment completed successfully."
else
    echo "No changes in the $BRANCH branch."
fi
