# rhj-settings-manager

Java Application to manage license, settings and build script within eclipse workspace

This quick and dirty solution scans all folders of the working directory's parent folder for a file name `.sharedsettings`.
If present, it overwrites (or creates) the files `LICENSE`, `settings.xml` and `.github/workflows/build.yml` with
the files found in the project `rhj-settings`.
