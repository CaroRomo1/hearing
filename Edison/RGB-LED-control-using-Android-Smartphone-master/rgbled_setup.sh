echo "src/gz all http://repo.opkg.net/edison/repo/all" > /etc/opkg/base-feeds.conf
echo "src/gz edison http://repo.opkg.net/edison/repo/edison" >> /etc/opkg/base-feeds.conf
echo "src/gz core2-32 http://repo.opkg.net/edison/repo/core2-32" >> /etc/opkg/base-feeds.conf
opkg update
mkdir /home/root/bluetooth
cd /home/root/bluetooth
wget https://software.intel.com/sites/default/files/managed/6c/16/bluetooth-service.tar.gz
tar -xvf bluetooth-service.tar.gz
rm bluetooth-service.tar.gz
cp bluetooth-spp-pin.service /lib/systemd/system
systemctl enable bluetooth-spp-pin
cd /home/root
opkg install python-pip
pip install pybluez
sleep 1
sed -i '8 c\
ExecStart=/usr/lib/bluez5/bluetooth/bluetoothd -C' /lib/systemd/system/bluetooth.service
sleep 2
reboot
