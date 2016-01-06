$script = <<SCRIPT
#!/usr/bin/env bash

apt-get update
apt-get -y install dos2unix git git-flow ghostscript imagemagick maven mc openjdk-7-jdk php5-cli screen

git config --global color.ui true
git config --global core.abbrev 10
git config --global core.editor nano
git config --global core.abbrev 10
git config --global --bool pull.rebase true
git config --global branch.autosetupmerge always
git config --global branch.autosetuprebase always
git config --global core.autocrlf input
git config --global core.safecrlf false
git config --global apply.whitespace nowarn
git config --global push.default simple
git config --global alias.ignore '!gi() { curl -L -s https://www.gitignore.io/api/$@ ;}; gi'
SCRIPT

Vagrant.configure(2) do |config|
  config.vm.box = "ubuntu/trusty64"
  config.vm.provision :shell, inline: $script
  config.ssh.shell = "bash -c 'BASH_ENV=/etc/profile exec bash'"
  config.vm.provider :virtualbox do |vb|
    vb.customize ["modifyvm", :id, "--memory", "2048"]
  end
end
