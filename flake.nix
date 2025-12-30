{
  description = "A flake for developing in this sbt project";
  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-25.11";
  };
  outputs = { self, nixpkgs }: 
    let
      pkgs = nixpkgs.legacyPackages.x86_64-linux;
    in
      {
      packages.x86_64-linux.hello = pkgs.coursier;
      packages.x86_64-linux.default = pkgs.coursier;
      devShells.x86_64-linux.default = pkgs.mkShell {
        packages = with pkgs; [
          git
          jdk21
          coursier
          zsh
          sqlite
        ];
        shellHook = ''
        zsh -c "echo starting flake...;exec zsh"
        export JAVA_HOME=${pkgs.jdk21}
        '';
      };
    };
}
