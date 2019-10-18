require_relative 'grid/grid'
require_relative 'algos/binary_tree'
require_relative 'seed'

seed = Seed.set(ENV['SEED'])

grid = Grid.new(4,4)
BinaryTree.on(grid, ENV['DEBUG'])

puts "Complete."
puts grid

puts "Seed: #{seed}"