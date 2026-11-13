# Build externo reproduzível (Linux x86_64 / CI)

Este procedimento compila o APK release do launcher fora do Termux/ARM (onde o AAPT2 empacotado pelo AGP pode falhar). Não instala APK no dispositivo e não altera o launcher padrão.

## Versões (fixadas no repositório)
- **Gradle**: 8.9 (`launcher/gradle/wrapper/gradle-wrapper.properties`, distribuição com checksum).
- **Android Gradle Plugin (AGP)**: 8.5.2 (`launcher/build.gradle`).
- **compileSdk / targetSdk**: 34 (`launcher/app/build.gradle`).
- **minSdk**: 26.
- **Build Tools**: 34.0.0 (recomendado/alinado ao CI do projeto).
- **JDK**: 17 (obrigatório para AGP 8.5.x/Gradle 8.9).
- **Dependências AndroidX**: `androidx.recyclerview:recyclerview:1.3.2`, `androidx.dynamicanimation:dynamicanimation:1.0.0` (resolvidas via Google/Maven Central).

## Pré-requisitos (Linux x86_64)
1. JDK 17 instalado e ativo (`JAVA_HOME` apontando para ele).
2. Android SDK com:
   - `platforms;android-34`
   - `build-tools;34.0.0`
   - `platform-tools` (opcional, só para instalar/depurar)
   - `cmdline-tools;latest` (para `sdkmanager`, se necessário)
3. Aceite de licenças (`sdkmanager --licenses`).
4. Variáveis de ambiente:
   - `JAVA_HOME=<caminho-do-jdk-17>`
   - `ANDROID_HOME` ou `ANDROID_SDK_ROOT=<caminho-do-sdk>`

## Comandos (release reproduzível)
```bash
# na raiz do repositório
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64   # ajuste conforme o host
export ANDROID_HOME="$HOME/Android/Sdk"               # ajuste conforme o host
export ANDROID_SDK_ROOT="$ANDROID_HOME"

cd launcher
./gradlew --no-daemon clean :app:assembleRelease
```

Saída esperada:
- APK: `launcher/app/build/outputs/apk/release/app-release.apk` (assinado com a chave de debug, como no buildType release do projeto; sideload via ADB, não publicação).
- Verificação de tamanho (gate do projeto: < 589824 bytes / 576 KB): `stat -c %s app/build/outputs/apk/release/app-release.apk`

## Exemplo de job (GitHub Actions, Ubuntu x86_64)
```yaml
name: launcher-release
on: [workflow_dispatch]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
      - uses: android-actions/setup-android@v3
      - name: Install SDK components
        run: sdkmanager "platforms;android-34" "build-tools;34.0.0" "platform-tools"
      - name: Build release APK
        working-directory: launcher
        run: ./gradlew --no-daemon clean :app:assembleRelease
      - name: Check APK size gate
        run: |
          SIZE=$(stat -c %s launcher/app/build/outputs/apk/release/app-release.apk)
          echo "APK size: $SIZE"
          test "$SIZE" -lt 589824
      - uses: actions/upload-artifact@v4
        with:
          name: app-release
          path: launcher/app/build/outputs/apk/release/app-release.apk
```

## Observações
- O build requer aapt2 compatível com x86_64/Linux (fornecido pelo Android Build Tools/AGP no ambiente padrão). Em Termux/ARM o aapt2 do cache do Gradle pode ser incompatível; por isso use Linux x86_64 ou CI.
- Não há etapas de instalação no dispositivo neste fluxo. O launcher padrão/HTC não é alterado por este procedimento.
