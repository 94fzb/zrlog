OS="$(uname)"
case $OS in
  Linux)
    OS='linux'
    sudo apt update
    sudo apt install expect -y
    ;;
  Darwin)
    OS='mac'
     brew update
     brew install expect
    ;;
  *)
    OS='windows'
    choco install -y putty
    ;;
esac