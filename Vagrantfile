# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.
Vagrant.configure(2) do |config|
  # Prefer VirtualBox before VMware Fusion
  config.vm.provider "virtualbox"

  config.vm.box = "ubuntu/xenial64"

  config.vm.provider "virtualbox" do |vm|
    vm.memory = 2048
  end

  config.vm.network "forwarded_port", guest: 3306, host: 3306

  config.vm.network "private_network", ip: "192.168.33.12"

  # Provision Script
  config.vm.provision :shell, path: "scripts/bootstrap-vagrant.sh"

  if Vagrant.has_plugin?("vagrant-cachier")
    # Configure cached packages to be shared between instances of the same base box.
    # More info on http://fgrehm.viewdocs.io/vagrant-cachier/usage
    config.cache.scope = :box
    config.cache.synced_folder_opts = {
      owner: "_apt",
    }
  end
end
