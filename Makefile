# We use boot for building.
# See http://boot-clj.com for details
# On Mac OS you cat get it with 'brew install boot-clj'
dev:
	boot dev

deploy:
	boot rel
	rm -rf rel_target/js/*.{edn,out}
	cd rel_target; tar -czf- --exclude '.*' * | ssh scdm.gosk.in "cd scdm/vk/d/m/ && rm -rf * && tar -xzvf- --warning=no-unknown-keyword"; cd -
