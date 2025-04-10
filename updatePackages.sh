#!/bin/bash

# Update model classes
for file in src/db/*.java; do
  sed -i '' 's/package com.game.ui;/package db;/g' "$file"
done

# Update UI main class
sed -i '' 's/package com.game.ui;/package db.ui;/g' src/db/ui/DatabaseUI.java
sed -i '' 's/import com.game.ui.panels.*;/import db.ui.panels.*;/g' src/db/ui/DatabaseUI.java

# Update panels
for file in src/db/ui/panels/*.java; do
  sed -i '' 's/package com.game.ui.panels;/package db.ui.panels;/g' "$file"
  sed -i '' 's/import com.game.ui.dialogs.*;/import db.ui.dialogs.*;/g' "$file"
  sed -i '' 's/import com.game.ui.*;/import db.*;/g' "$file"
  sed -i '' 's/com.game.ui.Character/db.Character/g' "$file"
  sed -i '' 's/com.game.ui.Player/db.Player/g' "$file"
  sed -i '' 's/com.game.ui.Ability/db.Ability/g' "$file"
  sed -i '' 's/com.game.ui.Location/db.Location/g' "$file"
  # Add explicit import if missing
  grep -q "import db.Character;" "$file" || sed -i '' '/^import/a\'$'\n''import db.Character;' "$file"
  grep -q "import db.Player;" "$file" || sed -i '' '/^import/a\'$'\n''import db.Player;' "$file"
  grep -q "import db.Ability;" "$file" || sed -i '' '/^import/a\'$'\n''import db.Ability;' "$file"
  grep -q "import db.Location;" "$file" || sed -i '' '/^import/a\'$'\n''import db.Location;' "$file"
done

# Update dialogs
for file in src/db/ui/dialogs/*.java; do
  sed -i '' 's/package com.game.ui.dialogs;/package db.ui.dialogs;/g' "$file"
  sed -i '' 's/import com.game.ui.*;/import db.*;/g' "$file"
  sed -i '' 's/com.game.ui.Character/db.Character/g' "$file"
  sed -i '' 's/com.game.ui.Player/db.Player/g' "$file"
  sed -i '' 's/com.game.ui.Ability/db.Ability/g' "$file"
  sed -i '' 's/com.game.ui.Location/db.Location/g' "$file"
  # Add explicit import if missing
  grep -q "import db.Character;" "$file" || sed -i '' '/^import/a\'$'\n''import db.Character;' "$file"
  grep -q "import db.Player;" "$file" || sed -i '' '/^import/a\'$'\n''import db.Player;' "$file"
  grep -q "import db.Ability;" "$file" || sed -i '' '/^import/a\'$'\n''import db.Ability;' "$file"
  grep -q "import db.Location;" "$file" || sed -i '' '/^import/a\'$'\n''import db.Location;' "$file"
done

echo "Package updates completed!" 