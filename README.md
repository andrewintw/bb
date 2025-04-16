# bb

Simple wrapper script for Yocto bitbake commands with a menu selection

```
$ ./bb help

Usage:
  bb image                - bake imx-image-core-mini
  bb kernel               - bake linux-imx
  bb uboot                - bake u-boot-imx
  bb imxboot              - bake imx-boot
  bb initrd               - bake fsl-image-mfgtool-initramfs

  bb rebuild <target>     - cleansstate + bake
  bb rebuild_full         - cleansstate for all targets and rebuild image

  bb config               - Setup build environment (create .env)
  bb copy_to_flash_folder - Copy generated images to the flash folder for flashing
  bb fetch                - Fetch Yocto packages

  bb                      - interactive menu
```

```
$ ./bb

Please select an option:
------------------------
  [ 1] bake imx-image-core-mini	| Build image for production
  [ 2] bake linux-imx			| Build Linux kernel
  [ 3] bake u-boot-imx			| Build U-Boot
  [ 4] bake imx-boot			| Create composite bootloader (SPL + U-Boot)
  [ 5] bake fsl-image-mfgtool-initramfs	| Generate Initramfs image

Rebuild targets:
------------------------
  [ 6] rebuild_full					| Clean + rebuild all targets (imxboot, uboot, kernel, image)
  [ 7] rebuild imx-image-core-mini	| Clean + rebuild image
  [ 8] rebuild linux-imx			| Clean + rebuild kernel
  [ 9] rebuild u-boot-imx			| Clean + rebuild U-Boot
  [10] rebuild imx-boot				| Clean + rebuild composite bootloader
  [11] rebuild fsl-image-mfgtool-initramfs| Clean + rebuild initramfs

Other actions:
------------------------
  [12] config				| Setup build environment
  [13] show config			| Display current configuration
  [14] fetch				| Fetch Yocto packages
  [15] copy_to_flash_folder	| Copy images to the flash folder for flashing
  [16] show env				| show bitbake environments

   [q] Exit

Enter the number (1-16):
```
